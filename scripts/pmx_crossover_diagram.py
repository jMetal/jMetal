"""Generate a diagram illustrating PMX (Partially Mapped Crossover).

The worked example replicates exactly the algorithm in
jmetal-core .../operator/crossover/impl/PMXCrossover.java (doCrossover):
 1. choose two cutting points,
 2. exchange the segment between them,
 3. build the value mapping defined by the exchanged segment,
 4. repair the positions outside the segment following the mapping chains.
"""
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import FancyArrowPatch

# --- PMX implementation faithful to PMXCrossover.doCrossover (dict-based mapping) ---
def pmx(p0, p1, cp1, cp2):
    n = len(p0)
    o0, o1 = list(p0), list(p1)
    rep1, rep2 = {}, {}
    for i in range(cp1, cp2 + 1):
        o0[i] = p1[i]
        o1[i] = p0[i]
        rep1[p1[i]] = p0[i]
        rep2[p0[i]] = p1[i]
    for i in range(n):
        if cp1 <= i <= cp2:
            continue
        n1 = p0[i]
        while n1 in rep1:
            n1 = rep1[n1]
        n2 = p1[i]
        while n2 in rep2:
            n2 = rep2[n2]
        o0[i] = n1
        o1[i] = n2
    return o0, o1

P1 = [1, 2, 3, 4, 5, 6, 7, 8, 9]
P2 = [4, 5, 3, 1, 8, 7, 6, 9, 2]
CP1, CP2 = 3, 6                       # 0-based, inclusive segment
C1, C2 = pmx(P1, P2, CP1, CP2)
assert sorted(C1) == P1 and sorted(C2) == P1, "offspring must be permutations"

# offspring right after the segment swap (before repair) -> shows the conflicts
def swapped(parent, donor):
    s = list(parent)
    for i in range(CP1, CP2 + 1):
        s[i] = donor[i]
    return s
S1 = swapped(P1, P2)   # child1 pre-repair
S2 = swapped(P2, P1)   # child2 pre-repair

# --- drawing helpers ---
CELL = 1.0
SEG_COLOR = "#ffe08a"
REPAIR_COLOR = "#bfe3c0"
CONFLICT_EDGE = "#c0392b"
PLAIN = "#ffffff"

fig, ax = plt.subplots(figsize=(11, 9))
ax.set_xlim(-2.4, len(P1) + 1.2)
ax.set_ylim(0, 21)
ax.axis("off")

def row(y, values, label, seg=True, face=None, repaired=None, conflict=None):
    """Draw one chromosome row at height y."""
    ax.text(-2.3, y + CELL / 2, label, ha="left", va="center", fontsize=11, fontweight="bold")
    for i, v in enumerate(values):
        if face is not None:
            color = face[i]
        elif seg and CP1 <= i <= CP2:
            color = SEG_COLOR
        else:
            color = PLAIN
        edge = CONFLICT_EDGE if (conflict and i in conflict) else "#333333"
        lw = 2.4 if (conflict and i in conflict) else 1.0
        ax.add_patch(plt.Rectangle((i, y), CELL, CELL, facecolor=color,
                                   edgecolor=edge, linewidth=lw))
        ax.text(i + CELL / 2, y + CELL / 2, str(v), ha="center", va="center", fontsize=12)

def cut_markers(y_lo, y_hi):
    for x in (CP1, CP2 + 1):
        ax.plot([x, x], [y_lo, y_hi], color=CONFLICT_EDGE, lw=1.6, ls=(0, (4, 3)))

def title(y, txt):
    ax.text(-2.3, y, txt, ha="left", va="center", fontsize=12.5,
            fontweight="bold", color="#1a5276")

# ---------- Step 1: parents + cut points ----------
title(20.2, "1. Two parents and two random cut points")
row(18.6, P1, "Parent 1")
row(17.3, P2, "Parent 2")
cut_markers(17.0, 19.9)

# ---------- Step 2: swap the segment ----------
title(15.6, "2. Exchange the segment between the cut points")
# child1 pre-repair: segment from P2 (highlighted), rest copied from P1
face1 = [SEG_COLOR if CP1 <= i <= CP2 else PLAIN for i in range(len(P1))]
# mark conflicts (values outside segment that duplicate a segment value)
seg_vals1 = set(S1[CP1:CP2 + 1])
conf1 = {i for i in range(len(S1)) if not (CP1 <= i <= CP2) and S1[i] in seg_vals1}
row(14.0, S1, "Child 1", face=face1, conflict=conf1)
seg_vals2 = set(S2[CP1:CP2 + 1])
conf2 = {i for i in range(len(S2)) if not (CP1 <= i <= CP2) and S2[i] in seg_vals2}
face2 = [SEG_COLOR if CP1 <= i <= CP2 else PLAIN for i in range(len(P2))]
row(12.7, S2, "Child 2", face=face2, conflict=conf2)
ax.text(len(P1) + 0.15, 14.5, "red = duplicated\n(needs repair)", fontsize=8.5,
        color=CONFLICT_EDGE, va="center")

# ---------- Step 3: mapping ----------
title(11.0, "3. Mapping defined by the exchanged segment")
mx = 1.0
for i in range(CP1, CP2 + 1):
    a, b = P1[i], P2[i]
    x = mx + (i - CP1) * 2.3
    ax.add_patch(plt.Rectangle((x, 9.2), CELL, CELL, facecolor=SEG_COLOR, edgecolor="#333"))
    ax.text(x + 0.5, 9.7, str(a), ha="center", va="center", fontsize=12)
    ax.add_patch(plt.Rectangle((x + 1.0, 9.2), CELL, CELL, facecolor=SEG_COLOR, edgecolor="#333"))
    ax.text(x + 1.5, 9.7, str(b), ha="center", va="center", fontsize=12)
    ax.annotate("", xy=(x + 1.0, 9.7), xytext=(x + 1.0, 9.7),
                arrowprops=dict(arrowstyle="<->", color="#333"))
    ax.text(x + 1.0, 8.85, "↔", ha="center", va="center", fontsize=10)

# ---------- Step 4: repaired children ----------
title(7.4, "4. Repair the outside positions following the mapping  →  valid children")
rep_face1 = [REPAIR_COLOR if (not (CP1 <= i <= CP2) and C1[i] != P1[i]) else
             (SEG_COLOR if CP1 <= i <= CP2 else PLAIN) for i in range(len(C1))]
row(5.8, C1, "Child 1", face=rep_face1)
rep_face2 = [REPAIR_COLOR if (not (CP1 <= i <= CP2) and C2[i] != P2[i]) else
             (SEG_COLOR if CP1 <= i <= CP2 else PLAIN) for i in range(len(C2))]
row(4.5, C2, "Child 2", face=rep_face2)
ax.text(len(P1) + 0.15, 5.2, "green = value\nfixed by mapping", fontsize=8.5,
        color="#1e7d34", va="center")

# legend
ax.add_patch(plt.Rectangle((0, 2.2), 0.7, 0.7, facecolor=SEG_COLOR, edgecolor="#333"))
ax.text(0.85, 2.55, "exchanged segment", fontsize=9, va="center")
ax.add_patch(plt.Rectangle((4.6, 2.2), 0.7, 0.7, facecolor=REPAIR_COLOR, edgecolor="#333"))
ax.text(5.45, 2.55, "repaired position", fontsize=9, va="center")

fig.suptitle("PMX – Partially Mapped Crossover", fontsize=16, fontweight="bold", y=0.98)
ax.text(len(P1) / 2 - 1.0, 0.8,
        "Goldberg & Lingle (1985) · permutation encoding · 2 parents → 2 children",
        ha="center", fontsize=9, color="#555")

import os
out = os.path.join(os.path.dirname(__file__), os.pardir,
                   "docs", "resources", "figures", "pmx_crossover.png")
fig.savefig(out, dpi=150, bbox_inches="tight")
print("saved:", out)
print("Parent1 :", P1)
print("Parent2 :", P2)
print("cut pts : indices", CP1, "..", CP2)
print("Child1  :", C1)
print("Child2  :", C2)
