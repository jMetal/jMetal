#!/usr/bin/env python3
"""
Interactive Pareto-front viewer (Plotly companion to plot_front.py).

Renders a single front (a FUN.csv produced by a jMetal algorithm run) interactively — rotate a 3D
front, zoom, hover for values — against its reference front. Objectives are auto-detected from the
column count: 2 -> 2D scatter, 3 -> 3D scatter, more than 3 -> parallel coordinates.

Comparison layout (--mode):
  overlay  both fronts on the same axes (default)
  side     two panels, Reference | Obtained, with shared axis ranges
  both     three panels, Reference | Obtained | Overlay

The reference front is passed explicitly as the optional second argument; its filename is NOT
guessed from the problem name (reference fronts follow no single naming convention).

Use this for manual exploration. For static, headless, report-embeddable figures use
plot_front.py instead.

Usage:
    python scripts/plot_front_interactive.py <FUN.csv> [<referenceFront.csv>]
        [--mode overlay|side|both] [--output figure.html] [--title TITLE]

With --output the interactive figure is written as a self-contained HTML file; otherwise it is shown
in the browser.
"""

import argparse
import sys
from pathlib import Path

import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
from plotly.subplots import make_subplots

FRONT_COLOR = "#1f77b4"
REF_COLOR = "#bbbbbb"


def load_front(path):
    if path is None or not Path(path).exists():
        return None
    return pd.read_csv(path, header=None)


def axis_ranges(*frames):
    """Per-axis (min, max) over the union of the given non-empty frames."""
    frames = [f for f in frames if f is not None and len(f)]
    if not frames:
        return None
    alld = pd.concat(frames, ignore_index=True)
    return [(float(alld[c].min()), float(alld[c].max())) for c in alld.columns]


def panels_for_mode(mode, front, ref):
    """Return a list of (subtitle, front_or_None, ref_or_None) panels."""
    if ref is None or mode == "overlay":
        return [("front vs reference" if ref is not None else "front", front, ref)]
    if mode == "side":
        return [("reference", None, ref), ("obtained", front, None)]
    return [("reference", None, ref), ("obtained", front, None), ("overlay", front, ref)]


def scatter(n_obj, data, name, color, size):
    if n_obj == 3:
        return go.Scatter3d(x=data[0], y=data[1], z=data[2], mode="markers", name=name,
                            marker=dict(size=size, color=color))
    return go.Scatter(x=data[0], y=data[1], mode="markers", name=name,
                      marker=dict(size=size, color=color))


def build_comparison(n_obj, panels, ranges, title):
    cell = {"type": "scene"} if n_obj == 3 else {"type": "xy"}
    fig = make_subplots(
        rows=1, cols=len(panels), specs=[[cell] * len(panels)],
        subplot_titles=[p[0] for p in panels] if len(panels) > 1 else None,
    )

    shown = set()  # show each legend entry only once across the panels
    for col, (_subtitle, panel_front, panel_ref) in enumerate(panels, start=1):
        for data, name, color, size in (
            (panel_ref, "reference", REF_COLOR, 3 if n_obj == 3 else 5),
            (panel_front, "front", FRONT_COLOR, 4 if n_obj == 3 else 7),
        ):
            if data is None or not len(data):
                continue
            trace = scatter(n_obj, data, name, color, size)
            trace.showlegend = name not in shown
            shown.add(name)
            fig.add_trace(trace, row=1, col=col)

        if ranges:
            if n_obj == 3:
                scene_name = "scene" if col == 1 else f"scene{col}"
                fig.update_layout(**{scene_name: dict(
                    xaxis=dict(range=ranges[0], title="f1"),
                    yaxis=dict(range=ranges[1], title="f2"),
                    zaxis=dict(range=ranges[2], title="f3"))})
            else:
                fig.update_xaxes(range=ranges[0], title_text="f1", row=1, col=col)
                fig.update_yaxes(range=ranges[1], title_text="f2", row=1, col=col)

    fig.update_layout(title=title)
    return fig


def figure_parallel(front, title):
    front = front.copy()
    front.columns = [f"f{i + 1}" for i in range(front.shape[1])]
    return px.parallel_coordinates(front, title=title)


def main():
    parser = argparse.ArgumentParser(
        description="Interactive Pareto-front viewer (Plotly companion to plot_front.py)."
    )
    parser.add_argument("front", type=Path, help="FUN.csv with the objective values")
    parser.add_argument("reference", type=Path, nargs="?", default=None,
                        help="reference front CSV (optional; pass the exact file)")
    parser.add_argument("--mode", choices=["overlay", "side", "both"], default="overlay",
                        help="comparison layout (default: overlay)")
    parser.add_argument("--output", type=Path, default=None,
                        help="write a self-contained interactive HTML here instead of showing it")
    parser.add_argument("--title", default=None, help="figure title")

    if len(sys.argv) == 1:
        parser.print_help()
        sys.exit(1)
    args = parser.parse_args()

    if not args.front.exists():
        print(f"ERROR: front file not found: {args.front}", file=sys.stderr)
        sys.exit(1)

    front = load_front(args.front)
    n_obj = front.shape[1]
    ref = load_front(args.reference)

    if args.mode in ("side", "both") and ref is None:
        print(f"note: --mode {args.mode} needs a reference front; falling back to a single panel",
              file=sys.stderr)

    title = args.title or args.front.stem

    if n_obj > 3:
        if ref is not None:
            print("note: reference ignored for >3 objectives (parallel coordinates)",
                  file=sys.stderr)
        fig = figure_parallel(front, title)
    else:
        panels = panels_for_mode(args.mode, front, ref)
        ranges = axis_ranges(front, ref)
        fig = build_comparison(n_obj, panels, ranges, title)

    if args.output:
        fig.write_html(str(args.output))
        print(f"Saved: {args.output}  ({len(front)} solutions, {n_obj} objectives, mode={args.mode})")
    else:
        fig.show()


if __name__ == "__main__":
    main()
