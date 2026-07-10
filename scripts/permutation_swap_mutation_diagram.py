"""Generate a diagram illustrating swap mutation on permutations.

The worked example replicates PermutationSwapMutation.doMutation
(jmetal-core .../operator/mutation/impl/PermutationSwapMutation.java):
two distinct positions are chosen at random and their elements are swapped.
Designed to complement the PMX crossover figure (same visual style).
"""
import os
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import FancyArrowPatch

# --- swap mutation faithful to PermutationSwapMutation ---
def swap_mutation(perm, pos1, pos2):
    p = list(perm)
    p[pos1], p[pos2] = p[pos2], p[pos1]
    return p

PERM = [1, 2, 3, 4, 5, 6, 7, 8, 9]
POS1, POS2 = 2, 6                      # 0-based positions to swap
MUT = swap_mutation(PERM, POS1, POS2)
assert sorted(MUT) == PERM, "result must stay a permutation"

CELL = 1.0
SELECT_COLOR = "#9ecae1"   # chosen positions (before)
CHANGED_COLOR = "#bfe3c0"  # swapped values (after)
PLAIN = "#ffffff"

fig, ax = plt.subplots(figsize=(11, 4.6))
ax.set_xlim(-2.4, len(PERM) + 1.6)
ax.set_ylim(0, 8.2)
ax.axis("off")

def row(y, values, label, face):
    ax.text(-2.3, y + CELL / 2, label, ha="left", va="center",
            fontsize=11, fontweight="bold")
    for i, v in enumerate(values):
        ax.add_patch(plt.Rectangle((i, y), CELL, CELL, facecolor=face[i],
                                   edgecolor="#333333", linewidth=1.0))
        ax.text(i + CELL / 2, y + CELL / 2, str(v), ha="center", va="center",
                fontsize=12)

def title(y, txt):
    ax.text(-2.3, y, txt, ha="left", va="center", fontsize=12.5,
            fontweight="bold", color="#1a5276")

# ---------- Before ----------
title(7.5, "1. Pick two distinct random positions")
face_before = [SELECT_COLOR if i in (POS1, POS2) else PLAIN for i in range(len(PERM))]
row(5.8, PERM, "Before", face_before)
# position markers
for p in (POS1, POS2):
    ax.text(p + CELL / 2, 5.45, f"pos {p}", ha="center", va="top", fontsize=8.5,
            color="#1f6390")
# curved double arrow between the two selected cells
arrow = FancyArrowPatch((POS1 + 0.5, 6.85), (POS2 + 0.5, 6.85),
                        connectionstyle="arc3,rad=-0.35",
                        arrowstyle="<|-|>", mutation_scale=16,
                        color="#c0392b", lw=1.8)
ax.add_patch(arrow)
ax.text((POS1 + POS2) / 2 + 0.5, 7.15, "swap", ha="center", va="bottom",
        fontsize=10, color="#c0392b", fontweight="bold")

# ---------- After ----------
title(4.0, "2. Swap their elements  →  mutated permutation")
face_after = [CHANGED_COLOR if i in (POS1, POS2) else PLAIN for i in range(len(MUT))]
row(2.3, MUT, "After", face_after)

# legend
ax.add_patch(plt.Rectangle((0, 0.6), 0.6, 0.6, facecolor=SELECT_COLOR, edgecolor="#333"))
ax.text(0.75, 0.9, "chosen positions", fontsize=9, va="center")
ax.add_patch(plt.Rectangle((4.4, 0.6), 0.6, 0.6, facecolor=CHANGED_COLOR, edgecolor="#333"))
ax.text(5.15, 0.9, "swapped values", fontsize=9, va="center")

fig.suptitle("Swap Mutation (permutation)", fontsize=16, fontweight="bold", y=1.0)
ax.text(len(PERM) / 2 - 1.0, 0.0,
        "applied with probability mutationProbability · keeps a valid permutation",
        ha="center", fontsize=9, color="#555")

out = os.path.join(os.path.dirname(__file__), os.pardir,
                   "docs", "resources", "figures", "permutation_swap_mutation.png")
fig.savefig(out, dpi=150, bbox_inches="tight")
print("saved:", out)
print("Before:", PERM)
print("swap positions:", POS1, POS2)
print("After :", MUT)
