"""Render the nine recorded two-objective ZDT runs and verify their reported metrics.

Run this script from any working directory. NumPy and Matplotlib are required.
Optimization results are read from the repository's FUN CSV files; no runs are
repeated and no approximation points are interpolated.
"""

from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
import re

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib.lines import Line2D
from matplotlib.ticker import MaxNLocator
import numpy as np


OUTPUT = Path(__file__).resolve().parent
ROOT = OUTPUT.parents[2]
PROBLEMS = ("ZDT1", "ZDT2", "ZDT3")
ALGORITHMS = ("RVEA", "RVEAStar", "iRVEA")
LABELS = {"RVEA": "RVEA", "RVEAStar": "RVEA*", "iRVEA": "iRVEA"}
COLORS = {"RVEA": "#D78327", "RVEAStar": "#2476BB", "iRVEA": "#168A77"}
SHAPES = {"ZDT1": "Convex front", "ZDT2": "Concave front", "ZDT3": "Disconnected front"}
INK = "#203347"
MUTED = "#617183"
REFERENCE_COLOR = "#7F8E9E"

plt.rcParams.update({
    "font.family": "DejaVu Sans", "font.size": 10,
    "text.color": INK, "axes.labelcolor": INK,
    "axes.edgecolor": "#C2CBD4", "xtick.color": MUTED, "ytick.color": MUTED,
    "axes.titleweight": "semibold", "figure.facecolor": "white",
    "savefig.facecolor": "white", "pdf.fonttype": 42,
})


def read_points(path: Path, columns: int) -> np.ndarray:
    values = np.loadtxt(path, delimiter=",", ndmin=2)
    if not len(values) or values.shape[1] != columns or not np.isfinite(values).all():
        raise ValueError(f"Expected finite data with {columns} columns: {path}")
    return values


def read_runs(input_dir):
    pattern = re.compile(
        r"(RVEA\*?|iRVEA) (ZDT[123]): seed=(\d+), evaluations=(\d+), "
        r"survivors=(\d+), nondominated=(\d+), IGD\+=([\d.]+), time=(\d+) ms"
    )
    matches = pattern.findall((OUTPUT / "runs.log").read_text(encoding="utf-8-sig"))
    if len(matches) != 9:
        raise ValueError("Expected exactly nine recorded successful runs")
    references = {
        problem: read_points(ROOT / f"resources/referenceFrontsCSV/{problem}.csv", 2)
        for problem in PROBLEMS
    }
    fronts = {}
    metadata = {}
    for label, problem, seed, evaluations, survivors, count, rounded_igd, runtime in matches:
        algorithm = label.replace("*", "Star")
        source = input_dir / f"FUN.{algorithm}.{problem}.csv"
        points = read_points(source, 2)
        variables = read_points(input_dir / f"VAR.{algorithm}.{problem}.csv", 30)
        assert len(points) == int(count) == len(variables)
        assert (variables >= 0).all() and (variables <= 1).all()
        assert int(seed) == 42 and int(evaluations) == 25000
        reference = references[problem]
        delta = np.maximum(points[None, :, :] - reference[:, None, :], 0)
        igd = float(np.sqrt((delta**2).sum(axis=2)).min(axis=1).mean())
        assert abs(igd - float(rounded_igd)) < 0.00000051
        # Verify that each plotted point is nondominated within its exported set.
        for point in points:
            assert not np.any(np.all(points <= point, axis=1) & np.any(points < point, axis=1))
        key = (problem, algorithm)
        fronts[key] = points
        metadata[key] = {
            "problem": problem, "algorithm": label, "objectives": 2, "variables": 30,
            "seed": int(seed), "evaluations": int(evaluations), "initial_population": 100,
            "survivors": int(survivors), "nondominated_points": int(count),
            "igd_plus": igd, "runtime_ms": int(runtime),
            "source": source.relative_to(ROOT).as_posix(),
            "sha256": hashlib.sha256(source.read_bytes()).hexdigest(),
            "reference_points": len(reference),
        }
    assert set(fronts) == {(p, a) for p in PROBLEMS for a in ALGORITHMS}
    return fronts, references, metadata


def draw_panel(axis, problem, algorithm, fronts, references, metadata):
    reference = references[problem]
    reference = reference[np.argsort(reference[:, 0])]
    # Preserve the gaps of ZDT3 rather than drawing false connecting segments.
    breaks = np.flatnonzero(np.diff(reference[:, 0]) > 0.02) + 1
    for segment in np.split(reference, breaks):
        axis.plot(*segment.T, color=REFERENCE_COLOR, linewidth=2.1,
                  linestyle="--", dash_capstyle="round", zorder=1)
    points = fronts[problem, algorithm]
    axis.scatter(*points.T, s=23, color=COLORS[algorithm], edgecolors="white",
                 linewidths=0.35, alpha=0.96, zorder=3)
    all_points = np.vstack([references[problem], *[fronts[problem, a] for a in ALGORITHMS]])
    low, high = all_points[:, 1].min(), all_points[:, 1].max()
    pad = (high - low) * 0.07
    axis.set(xlim=(-0.035, 1.035), ylim=(low - pad, high + pad), xlabel="$f_1$", ylabel="$f_2$")
    axis.set_title(f"{problem} / {LABELS[algorithm]}", fontsize=13, loc="left", pad=29)
    row = metadata[problem, algorithm]
    axis.text(0, 1.035,
              f"{row['nondominated_points']} nondominated points  |  IGD+ {row['igd_plus']:.6f}",
              transform=axis.transAxes, fontsize=9, color=MUTED)
    axis.grid(color="#E4EAF0", linewidth=0.65)
    axis.set_axisbelow(True)
    axis.spines[["top", "right"]].set_visible(False)
    axis.xaxis.set_major_locator(MaxNLocator(nbins=5))
    axis.yaxis.set_major_locator(MaxNLocator(nbins=5))
    axis.tick_params(length=0, pad=5, labelsize=9)


def decorate(figure, title, subtitle):
    figure.text(0.065, 0.971, title, fontsize=23, weight="bold", va="top")
    figure.text(0.065, 0.932, subtitle, fontsize=11, color=MUTED, va="top")
    figure.text(0.065, 0.025,
                "Seed 42  |  25,000 evaluations  |  Initial population 100  |  Single runs",
                fontsize=9, color=MUTED)
    handles = [
        Line2D([], [], color=REFERENCE_COLOR, linestyle="--", linewidth=2,
               label="Reference Pareto front"),
        Line2D([], [], marker="o", color=INK, linestyle="none", markersize=5,
               label="Colored points: obtained solutions"),
    ]
    figure.legend(handles=handles, loc="lower right", bbox_to_anchor=(0.97, 0.009),
                  ncols=2, frameon=False, fontsize=8, handletextpad=0.5)


def overview(fronts, references, metadata):
    figure = plt.figure(figsize=(15.5, 12.8))
    decorate(figure, "RVEA family / three simpler two-objective problems",
             "ZDT1: convex  |  ZDT2: concave  |  ZDT3: disconnected. "
             "30 decision variables in each problem.")
    grid = figure.add_gridspec(3, 3, left=0.065, right=0.97, bottom=0.10,
                              top=0.85, hspace=0.48, wspace=0.24)
    for row, problem in enumerate(PROBLEMS):
        for column, algorithm in enumerate(ALGORITHMS):
            draw_panel(figure.add_subplot(grid[row, column]), problem, algorithm,
                       fronts, references, metadata)
    return figure


def individual(problem, fronts, references, metadata):
    figure = plt.figure(figsize=(15.5, 5.9))
    decorate(figure, f"{problem} / {SHAPES[problem].lower()}",
             "Two objectives, 30 variables. Identical axes across algorithms; lower IGD+ is better.")
    # Extra separation keeps the subtitle clear of the main heading on a short page.
    figure.texts[1].set_y(0.902)
    grid = figure.add_gridspec(1, 3, left=0.065, right=0.97, bottom=0.17,
                              top=0.72, wspace=0.24)
    for column, algorithm in enumerate(ALGORITHMS):
        draw_panel(figure.add_subplot(grid[0, column]), problem, algorithm,
                   fronts, references, metadata)
    return figure


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input-dir", type=Path, default=ROOT / "results/rvea/data")
    fronts, references, metadata = read_runs(parser.parse_args().input_dir.resolve())
    (OUTPUT / "plot_data.json").write_text(
        json.dumps(list(metadata.values()), indent=2) + "\n", encoding="utf-8")
    figures = [("overview", overview(fronts, references, metadata))]
    figures.extend((f"{problem}_comparison", individual(problem, fronts, references, metadata))
                   for problem in PROBLEMS)
    with PdfPages(OUTPUT / "ZDT_execution_plots.pdf") as pdf:
        pdf.infodict()["Title"] = "RVEA, RVEA*, and iRVEA on ZDT1, ZDT2, and ZDT3"
        for name, figure in figures:
            figure.savefig(OUTPUT / f"{name}.png", dpi=180)
            pdf.savefig(figure)
            plt.close(figure)
            print(f"Saved {name}.png")
    print("Verified nine runs; saved four PNGs and a four-page PDF.")


if __name__ == "__main__":
    main()
