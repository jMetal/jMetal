"""Generate a diagram illustrating bit-flip mutation on a binary string.

The worked example replicates BitFlipMutation.doMutation
(jmetal-core .../operator/mutation/impl/BitFlipMutation.java): every bit is flipped
independently with probability mutationProbability. Here a few bits are flipped to
show the effect.
"""
import os
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

X = [1, 0, 1, 1, 0, 0, 1, 0, 1, 1]
FLIP = {1, 5, 8}  # positions that are flipped in this example
MUT = [1 - b if i in FLIP else b for i, b in enumerate(X)]

CELL = 1.0
FLIP_SELECT = "#f4a6a0"   # bits selected to flip (before)
FLIP_DONE = "#bfe3c0"     # flipped bits (after)
PLAIN = "#ffffff"
N = len(X)

fig, ax = plt.subplots(figsize=(10.5, 4.6))
ax.set_xlim(-2.4, N + 1.4)
ax.set_ylim(0, 8.2)
ax.axis("off")

def row(y, values, label, face):
    ax.text(-2.3, y + CELL / 2, label, ha="left", va="center", fontsize=11,
            fontweight="bold")
    for i, v in enumerate(values):
        ax.add_patch(plt.Rectangle((i, y), CELL, CELL, facecolor=face[i],
                                   edgecolor="#333333", linewidth=1.0))
        ax.text(i + CELL / 2, y + CELL / 2, str(v), ha="center", va="center",
                fontsize=12)

def title(y, txt):
    ax.text(-2.3, y, txt, ha="left", va="center", fontsize=12.5,
            fontweight="bold", color="#1a5276")

# ---------- Before ----------
title(7.5, "1. Each bit flips independently with probability p")
face_before = [FLIP_SELECT if i in FLIP else PLAIN for i in range(N)]
row(5.8, X, "Before", face_before)
for i in FLIP:
    ax.text(i + CELL / 2, 5.45, "flip", ha="center", va="top", fontsize=8.5,
            color="#c0392b")

# ---------- After ----------
title(4.0, "2. Selected bits are inverted  →  mutated string")
face_after = [FLIP_DONE if i in FLIP else PLAIN for i in range(N)]
row(2.3, MUT, "After", face_after)

# legend
ax.add_patch(plt.Rectangle((0, 0.6), 0.6, 0.6, facecolor=FLIP_SELECT, edgecolor="#333"))
ax.text(0.75, 0.9, "bits that flip", fontsize=9, va="center")
ax.add_patch(plt.Rectangle((4.4, 0.6), 0.6, 0.6, facecolor=FLIP_DONE, edgecolor="#333"))
ax.text(5.15, 0.9, "flipped bits", fontsize=9, va="center")

fig.suptitle("Bit-Flip Mutation (binary)", fontsize=16, fontweight="bold", y=1.0)
ax.text(N / 2 - 1.0, 0.0,
        "binary encoding · 0↔1 · typical probability p = 1 / (number of bits)",
        ha="center", fontsize=9, color="#555")

out = os.path.join(os.path.dirname(__file__), os.pardir,
                   "docs", "resources", "figures", "bit_flip_mutation.png")
fig.savefig(out, dpi=150, bbox_inches="tight")
print("saved:", out)
print("Before:", X)
print("flip  :", sorted(FLIP))
print("After :", MUT)
