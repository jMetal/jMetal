#!/usr/bin/env python3
"""
Plot a single Pareto front (a FUN.csv produced by a jMetal algorithm run) against its reference
front.

Objectives are auto-detected from the column count: 2 -> 2D scatter, 3 -> 3D scatter, more than 3
-> parallel coordinates.

Comparison layout (--mode):
  overlay  both fronts on the same axes (default)
  side     two panels, Reference | Obtained, with shared axis ranges
  both     three panels, Reference | Obtained | Overlay

The reference front is passed explicitly as the optional second argument; its filename is NOT
guessed from the problem name, because reference fronts do not follow a single naming convention
(e.g. DTLZ1 with 3 objectives is `DTLZ1.3D.csv` and with 2 it is `DTLZ1.2D.csv`, while RE/ZDT fronts
are plain `<problem>.csv`).

Usage:
    python scripts/plot_front.py <FUN.csv> [<referenceFront.csv>] [--mode overlay|side|both]
        [--output figure.png] [--title TITLE]

By default the figure is written next to the FUN file (<FUN_stem>.png).
"""

import argparse
import sys
from pathlib import Path

import matplotlib

matplotlib.use("Agg")

import matplotlib.pyplot as plt  # noqa: E402
import numpy as np  # noqa: E402
import pandas as pd  # noqa: E402
from mpl_toolkits.mplot3d import Axes3D  # noqa: F401,E402  (registers the 3d projection)

FRONT_COLOR = "#1f77b4"
REF_COLOR = "#bbbbbb"


def load_front(path):
    if path is None or not Path(path).exists():
        return None
    return pd.read_csv(path, header=None).values


def axis_ranges(*arrays):
    """Per-axis (min, max) over the union of the given non-empty arrays."""
    arrays = [a for a in arrays if a is not None and len(a)]
    if not arrays:
        return None
    stacked = np.vstack(arrays)
    return [(float(stacked[:, i].min()), float(stacked[:, i].max()))
            for i in range(stacked.shape[1])]


def draw_2d(ax, front, ref, ranges, title):
    if ref is not None and len(ref):
        ax.scatter(ref[:, 0], ref[:, 1], s=8, c=REF_COLOR, label="reference", edgecolors="none")
    if front is not None and len(front):
        ax.scatter(front[:, 0], front[:, 1], s=14, c=FRONT_COLOR, label="front", edgecolors="none")
    ax.set_xlabel("$f_1$")
    ax.set_ylabel("$f_2$")
    ax.set_title(title)
    if ranges:
        ax.set_xlim(ranges[0])
        ax.set_ylim(ranges[1])
    ax.legend(fontsize=8)


def draw_3d(ax, front, ref, ranges, title):
    if ref is not None and len(ref):
        ax.scatter(ref[:, 0], ref[:, 1], ref[:, 2], s=6, c=REF_COLOR,
                   label="reference", edgecolors="none")
    if front is not None and len(front):
        ax.scatter(front[:, 0], front[:, 1], front[:, 2], s=12, c=FRONT_COLOR,
                   label="front", edgecolors="none")
    ax.set_xlabel("$f_1$")
    ax.set_ylabel("$f_2$")
    ax.set_zlabel("$f_3$")
    ax.set_title(title)
    if ranges:
        ax.set_xlim(ranges[0])
        ax.set_ylim(ranges[1])
        ax.set_zlim(ranges[2])
    ax.view_init(elev=25, azim=45)
    ax.legend(fontsize=8)


def plot_parallel(ax, front, title):
    n_obj = front.shape[1]
    xs = list(range(n_obj))
    for row in front:
        ax.plot(xs, row, color=FRONT_COLOR, alpha=0.4, lw=0.8)
    ax.set_xticks(xs)
    ax.set_xticklabels([f"$f_{{{i + 1}}}$" for i in range(n_obj)])
    ax.set_title(title)


def panels_for_mode(mode, front, ref):
    """Return a list of (subtitle, front_or_None, ref_or_None) panels."""
    if ref is None or mode == "overlay":
        return [("front vs reference" if ref is not None else "front", front, ref)]
    if mode == "side":
        return [("reference", None, ref), ("obtained", front, None)]
    return [("reference", None, ref), ("obtained", front, None), ("overlay", front, ref)]


def main():
    parser = argparse.ArgumentParser(
        description="Plot a single Pareto front against its reference front."
    )
    parser.add_argument("front", type=Path, help="FUN.csv with the objective values")
    parser.add_argument("reference", type=Path, nargs="?", default=None,
                        help="reference front CSV (optional; pass the exact file)")
    parser.add_argument("--mode", choices=["overlay", "side", "both"], default="overlay",
                        help="comparison layout (default: overlay)")
    parser.add_argument("--output", type=Path, default=None,
                        help="output image (default: <front>.png)")
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
        fig, ax = plt.subplots(figsize=(7, 6))
        plot_parallel(ax, front, title)
    else:
        panels = panels_for_mode(args.mode, front, ref)
        ranges = axis_ranges(front, ref)
        fig = plt.figure(figsize=(6 * len(panels), 5.5))
        if len(panels) > 1:
            fig.suptitle(title, fontweight="bold")
        for i, (subtitle, panel_front, panel_ref) in enumerate(panels, start=1):
            label = subtitle if len(panels) > 1 else title
            if n_obj == 3:
                ax = fig.add_subplot(1, len(panels), i, projection="3d")
                draw_3d(ax, panel_front, panel_ref, ranges, label)
            else:
                ax = fig.add_subplot(1, len(panels), i)
                draw_2d(ax, panel_front, panel_ref, ranges, label)

    fig.tight_layout()
    output = args.output or args.front.with_suffix(".png")
    fig.savefig(output, dpi=120, bbox_inches="tight")
    plt.close(fig)
    print(f"Saved: {output}  ({len(front)} solutions, {n_obj} objectives, mode={args.mode})")


if __name__ == "__main__":
    main()
