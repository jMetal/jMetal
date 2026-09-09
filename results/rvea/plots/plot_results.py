"""Plot the five existing RVEA-family example runs without rerunning optimization.

Run from any working directory with Python, NumPy, and Matplotlib installed.
The input files contain exported nondominated solutions, not all final survivors.
"""

from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
import numpy as np
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib.lines import Line2D
from matplotlib.ticker import MaxNLocator


ROOT = Path(__file__).resolve().parents[3]
OUTPUT = Path(__file__).resolve().parent
COLORS = {"RVEA": "#D78327", "RVEAStar": "#2476BB", "iRVEA": "#168A77"}
LABELS = {"RVEA": "RVEA", "RVEAStar": "RVEA*", "iRVEA": "iRVEA"}
RUNS = [
    ("DTLZ2", "RVEAStar"),
    ("DTLZ7", "iRVEA"),
    ("DTLZ5", "RVEA"),
    ("DTLZ5", "RVEAStar"),
    ("DTLZ5", "iRVEA"),
]
REFERENCE_COLOR = "#A9B4BF"
INK = "#203347"
MUTED = "#617183"

plt.rcParams.update({
    "font.family": "DejaVu Sans",
    "font.size": 10,
    "text.color": INK,
    "axes.labelcolor": INK,
    "axes.edgecolor": "#B8C2CD",
    "xtick.color": MUTED,
    "ytick.color": MUTED,
    "axes.titlesize": 13,
    "axes.titleweight": "semibold",
    "figure.facecolor": "white",
    "savefig.facecolor": "white",
    "pdf.fonttype": 42,
})


def read_points(path: Path) -> np.ndarray:
    points = np.loadtxt(path, delimiter=",", ndmin=2)
    if points.shape[1] != 3 or not len(points) or not np.isfinite(points).all():
        raise ValueError(f"Expected a nonempty finite three-objective front: {path}")
    return points


def igd_plus(points: np.ndarray, reference: np.ndarray) -> float:
    # Minimization: penalize only objective components worse than the reference.
    distances = []
    for batch in np.array_split(reference, max(1, len(reference) // 500)):
        differences = np.maximum(points[None, :, :] - batch[:, None, :], 0)
        distances.extend(np.sqrt(np.sum(differences**2, axis=2)).min(axis=1))
    return float(np.mean(distances))


def axis_limits(problem: str, fronts: dict, references: dict) -> tuple:
    values = [references[problem]]
    values.extend(front for (name, _), front in fronts.items() if name == problem)
    maximum = np.vstack(values).max(axis=0)
    if problem == "DTLZ7":
        return ((0, 0.92), (0, 0.92), (2.4, 6.2))
    upper = max(1.05, float(maximum.max()) * 1.04)
    return ((0, upper),) * 3


def reference_points(axis, problem: str, reference: np.ndarray) -> None:
    if problem == "DTLZ5":
        ordered = reference[np.argsort(reference[:, 0])]
        axis.plot(*ordered.T, color=REFERENCE_COLOR, lw=2.1, alpha=0.95, zorder=1)
    else:
        # Deterministic display subsampling; IGD+ uses every reference point.
        stride = max(1, len(reference) // 1800)
        display = reference[::stride]
        axis.scatter(*display.T, s=3 if problem == "DTLZ2" else 7,
                     color=REFERENCE_COLOR, alpha=0.30, depthshade=False,
                     edgecolors="none", rasterized=True)


def draw_front(axis, problem: str, algorithm: str, fronts: dict,
               references: dict, metrics: dict, title: str | None = None) -> None:
    points = fronts[problem, algorithm]
    reference_points(axis, problem, references[problem])
    axis.scatter(*points.T, color=COLORS[algorithm], s=29,
                 edgecolors="white", linewidths=0.4, depthshade=False, zorder=5)
    limits = axis_limits(problem, fronts, references)
    axis.set(xlim=limits[0], ylim=limits[1], zlim=limits[2])
    axis.set_box_aspect((1, 1, 0.88))
    axis.view_init(elev=24, azim=-53 if problem != "DTLZ5" else -62)
    for index, dimension in enumerate((axis.xaxis, axis.yaxis, axis.zaxis), 1):
        dimension.set_major_locator(MaxNLocator(nbins=4))
        dimension.set_pane_color((0.966, 0.975, 0.984, 1))
        dimension._axinfo["grid"].update(color="#DAE1E8", linewidth=0.55)
        dimension.set_tick_params(labelsize=8, pad=0)
    axis.set_xlabel("$f_1$", labelpad=2)
    axis.set_ylabel("$f_2$", labelpad=2)
    axis.set_zlabel("$f_3$", labelpad=2)
    axis.set_title(title or f"{problem} / {LABELS[algorithm]}", pad=12, loc="center")
    axis.text2D(0.5, 0.995,
                f"{len(points)} nondominated points  |  IGD+ {metrics[problem, algorithm]:.6f}",
                transform=axis.transAxes, ha="center", va="top", fontsize=9, color=MUTED)


def heading(figure, title: str, subtitle: str) -> None:
    figure.text(0.045, 0.961, title, fontsize=22, weight="bold", va="top")
    figure.text(0.045, 0.916, subtitle, fontsize=11, color=MUTED, va="top")


def footer(figure, text: str | None = None) -> None:
    figure.text(0.045, 0.034,
                text or "Seed 42  |  25,025 evaluations per run  |  One run per configuration",
                fontsize=9, color=MUTED)
    handles = [
        Line2D([], [], marker="o", linestyle="none", markersize=5,
               color=REFERENCE_COLOR, label="Reference Pareto front"),
        Line2D([], [], marker="o", linestyle="none", markersize=5,
               color=INK, label="Colored points: obtained solutions"),
    ]
    figure.legend(handles=handles, loc="lower right", bbox_to_anchor=(0.955, 0.017),
                  frameon=False, fontsize=8, ncols=2, handletextpad=0.4)


def overview(fronts: dict, references: dict, metrics: dict):
    figure = plt.figure(figsize=(16, 10.5))
    heading(figure, "RVEA family / execution results",
            "Five recorded runs on three-objective DTLZ problems. Lower IGD+ is better.")
    grid = figure.add_gridspec(2, 3, left=0.045, right=0.955, bottom=0.105,
                              top=0.83, wspace=0.14, hspace=0.29)
    draw_front(figure.add_subplot(grid[0, 0], projection="3d"),
               "DTLZ2", "RVEAStar", fronts, references, metrics)
    draw_front(figure.add_subplot(grid[0, 1], projection="3d"),
               "DTLZ7", "iRVEA", fronts, references, metrics)
    axis = figure.add_subplot(grid[0, 2])
    algorithms = ["RVEA", "RVEAStar", "iRVEA"]
    values = [metrics["DTLZ5", algorithm] for algorithm in algorithms]
    axis.barh(range(3), values, height=0.48,
              color=[COLORS[algorithm] for algorithm in algorithms])
    axis.set_yticks(range(3), [LABELS[algorithm] for algorithm in algorithms])
    axis.invert_yaxis()
    axis.set_xlim(0, max(values) * 1.43)
    axis.set_ylim(2.7, -0.9)
    axis.set_title("DTLZ5 / IGD+ comparison", loc="left", pad=16)
    axis.set_xlabel("IGD+ (lower is better)", fontsize=10, labelpad=8)
    axis.xaxis.set_major_locator(MaxNLocator(nbins=3))
    axis.grid(axis="x", color="#E4EAF0", linewidth=0.7)
    axis.set_axisbelow(True)
    for spine in axis.spines.values():
        spine.set_visible(False)
    axis.tick_params(axis="both", length=0, labelsize=9)
    for index, value in enumerate(values):
        axis.text(value + max(values) * 0.045, index, f"{value:.6f}",
                   va="center", fontsize=10, color=INK)
    axis.text(0, 0.035, "Same problem, budget, and seed.",
               transform=axis.transAxes, fontsize=8, color=MUTED)
    for column, algorithm in enumerate(algorithms):
        draw_front(figure.add_subplot(grid[1, column], projection="3d"),
                   "DTLZ5", algorithm, fronts, references, metrics)
    footer(figure)
    return figure


def single_problem(problem: str, algorithm: str, fronts: dict,
                   references: dict, metrics: dict):
    figure = plt.figure(figsize=(12, 6.7))
    description = "Regular front" if problem == "DTLZ2" else "Disconnected front"
    heading(figure, f"{problem} / {LABELS[algorithm]}",
            f"{description}  |  {len(fronts[problem, algorithm])} nondominated solutions"
            f"  |  IGD+ {metrics[problem, algorithm]:.6f}")
    grid = figure.add_gridspec(1, 2, left=0.06, right=0.94,
                              bottom=0.19, top=0.80, wspace=0.26)
    axis = figure.add_subplot(grid[0, 0], projection="3d")
    draw_front(axis, problem, algorithm, fronts, references, metrics, title="Objective space")
    axis.texts[0].set_visible(False)
    projection = figure.add_subplot(grid[0, 1])
    reference = references[problem]
    points = fronts[problem, algorithm]
    projection.scatter(reference[:, 0], reference[:, 1], s=5,
                       color=REFERENCE_COLOR, alpha=0.25, edgecolors="none", rasterized=True)
    projection.scatter(points[:, 0], points[:, 1], s=32, color=COLORS[algorithm],
                       edgecolors="white", linewidths=0.5, zorder=5)
    limits = axis_limits(problem, fronts, references)
    projection.set(xlabel="$f_1$", ylabel="$f_2$", xlim=limits[0], ylim=limits[1])
    projection.set_title("Projection onto $f_1$ and $f_2$", pad=16)
    projection.set_aspect("equal", adjustable="box")
    projection.grid(color="#E5EAF0", linewidth=0.6)
    projection.set_axisbelow(True)
    projection.spines[["top", "right"]].set_visible(False)
    footer(figure)
    return figure


def dtlz5_comparison(fronts: dict, references: dict, metrics: dict):
    figure = plt.figure(figsize=(16, 7))
    heading(figure, "DTLZ5 / comparison on a degenerate front",
            "Three objectives; the reference Pareto front is a curve. All panels share axis limits.")
    grid = figure.add_gridspec(1, 3, left=0.04, right=0.95, bottom=0.18,
                              top=0.79, wspace=0.10)
    for column, algorithm in enumerate(("RVEA", "RVEAStar", "iRVEA")):
        draw_front(figure.add_subplot(grid[0, column], projection="3d"),
                   "DTLZ5", algorithm, fronts, references, metrics, title=LABELS[algorithm])
    footer(figure)
    return figure


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input-dir", type=Path, default=ROOT / "results/rvea/data")
    input_dir = parser.parse_args().input_dir.resolve()
    references = {
        problem: read_points(ROOT / "resources/referenceFrontsCSV" / f"{problem}.3D.csv")
        for problem in ("DTLZ2", "DTLZ5", "DTLZ7")
    }
    fronts = {
        (problem, algorithm): read_points(input_dir / f"FUN.{algorithm}.{problem}.csv")
        for problem, algorithm in RUNS
    }
    metrics = {key: igd_plus(points, references[key[0]]) for key, points in fronts.items()}
    report = []
    for problem, algorithm in RUNS:
        source = input_dir / f"FUN.{algorithm}.{problem}.csv"
        report.append({
            "problem": problem, "algorithm": LABELS[algorithm],
            "seed": 42, "evaluations": 25025,
            "metadata_source": "docs/rvea-variants.md (recorded execution settings)",
            "nondominated_points": len(fronts[problem, algorithm]),
            "igd_plus": metrics[problem, algorithm],
            "source": source.relative_to(ROOT).as_posix(),
            "sha256": hashlib.sha256(source.read_bytes()).hexdigest(),
        })
    (OUTPUT / "plot_data.json").write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    figures = [
        ("overview", overview(fronts, references, metrics)),
        ("DTLZ2_RVEAStar", single_problem("DTLZ2", "RVEAStar", fronts, references, metrics)),
        ("DTLZ7_iRVEA", single_problem("DTLZ7", "iRVEA", fronts, references, metrics)),
        ("DTLZ5_comparison", dtlz5_comparison(fronts, references, metrics)),
    ]
    with PdfPages(OUTPUT / "RVEA_execution_plots.pdf") as pdf:
        pdf.infodict()["Title"] = "RVEA family: plots from recorded example executions"
        for name, figure in figures:
            figure.savefig(OUTPUT / f"{name}.png", dpi=180)
            pdf.savefig(figure)
            plt.close(figure)
            print(f"Saved {name}.png")
    print("Saved RVEA_execution_plots.pdf (4 pages)")


if __name__ == "__main__":
    main()
