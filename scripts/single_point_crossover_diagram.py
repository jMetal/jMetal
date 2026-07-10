"""Generate a diagram illustrating single-point crossover on binary strings.

The worked example replicates SinglePointCrossover.doCrossover
(jmetal-core .../operator/crossover/impl/SinglePointCrossover.java): a crossover
point is chosen and the bits from that point onwards are swapped between the two
parents, producing two children. Bits are coloured by the parent they come from.
"""
import os
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

P1 = [1, 0, 1, 1, 0, 0, 1, 0, 1, 1]
P2 = [0, 1, 1, 0, 1, 1, 0, 1, 0, 0]
POINT = 6  # bits at indices >= POINT are swapped (tail)

C1 = P1[:POINT] + P2[POINT:]
C2 = P2[:POINT] + P1[POINT:]

CELL = 1.0
P1_COLOR = "#9ecae1"   # bits coming from parent 1
P2_COLOR = "#fdbb84"   # bits coming from parent 2
N = len(P1)

fig, ax = plt.subplots(figsize=(10.5, 5.0))
ax.set_xlim(-2.4, N + 1.4)
ax.set_ylim(0, 11.5)
ax.axis("off")

def row(y, values, label, src):
    """src[i] is 1 or 2: which parent the bit comes from (sets the colour)."""
    ax.text(-2.3, y + CELL / 2, label, ha="left", va="center", fontsize=11,
            fontweight="bold")
    for i, v in enumerate(values):
        color = P1_COLOR if src[i] == 1 else P2_COLOR
        ax.add_patch(plt.Rectangle((i, y), CELL, CELL, facecolor=color,
                                   edgecolor="#333333", linewidth=1.0))
        ax.text(i + CELL / 2, y + CELL / 2, str(v), ha="center", va="center",
                fontsize=12)

def cut(y_lo, y_hi):
    ax.plot([POINT, POINT], [y_lo, y_hi], color="#c0392b", lw=1.8, ls=(0, (4, 3)))

def title(y, txt):
    ax.text(-2.3, y, txt, ha="left", va="center", fontsize=12.5,
            fontweight="bold", color="#1a5276")

# ---------- Step 1 ----------
title(11.0, "1. Pick a crossover point")
row(9.4, P1, "Parent 1", [1] * N)
row(8.1, P2, "Parent 2", [2] * N)
cut(7.8, 10.7)
ax.text(POINT, 10.85, "crossover point", ha="center", va="bottom", fontsize=9,
        color="#c0392b")

# ---------- Step 2 ----------
title(6.3, "2. Swap the tails after the point  →  children")
row(4.7, C1, "Child 1", [1] * POINT + [2] * (N - POINT))
row(3.4, C2, "Child 2", [2] * POINT + [1] * (N - POINT))
cut(3.1, 5.7)

# legend
ax.add_patch(plt.Rectangle((0, 1.2), 0.6, 0.6, facecolor=P1_COLOR, edgecolor="#333"))
ax.text(0.75, 1.5, "from parent 1", fontsize=9, va="center")
ax.add_patch(plt.Rectangle((4.4, 1.2), 0.6, 0.6, facecolor=P2_COLOR, edgecolor="#333"))
ax.text(5.15, 1.5, "from parent 2", fontsize=9, va="center")

fig.suptitle("Single-Point Crossover (binary)", fontsize=16, fontweight="bold", y=1.0)
ax.text(N / 2 - 1.0, 0.3,
        "binary encoding · 2 parents → 2 children · applied with probability "
        "crossoverProbability",
        ha="center", fontsize=9, color="#555")

out = os.path.join(os.path.dirname(__file__), os.pardir,
                   "docs", "resources", "figures", "single_point_crossover.png")
fig.savefig(out, dpi=150, bbox_inches="tight")
print("saved:", out)
print("Parent1:", P1)
print("Parent2:", P2)
print("point  :", POINT)
print("Child1 :", C1)
print("Child2 :", C2)
