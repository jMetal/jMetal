"""Generate a diagram illustrating BLX-alpha-beta crossover on a real variable.

The construction replicates BLXAlphaBetaCrossover.execute
(jmetal-core .../operator/crossover/impl/BLXAlphaBetaCrossover.java) for one variable:
with p1 = min(parents), p2 = max(parents) and d = p2 - p1, offspring are drawn
uniformly from [p1 - alpha*d, p2 + beta*d]. The interval is extended asymmetrically:
alpha below the smaller parent, beta above the larger one.
"""
import os
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

P1, P2 = 3.0, 7.0          # parents (p1 < p2)
ALPHA, BETA = 0.3, 0.7     # asymmetric extensions
LB, UB = 0.0, 10.0
d = P2 - P1
CMIN = P1 - ALPHA * d
CMAX = P2 + BETA * d

N = 400_000
rng = np.random.default_rng(3)
offspring = CMIN + rng.random(N) * (CMAX - CMIN)
offspring = np.clip(offspring, LB, UB)

RANGE_COLOR = "#9ecae1"    # between parents
EXT_COLOR = "#fdbb84"      # alpha/beta extensions

fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(10.5, 5.4),
                               gridspec_kw={"height_ratios": [1.0, 1.1]})

# ---------- Panel 1: interval schematic ----------
ax1.set_xlim(LB - 0.3, UB + 0.3)
ax1.set_ylim(0, 1)
ax1.axis("off")
ax1.set_title("1. Sampling interval  [p1 - α·d ,  p2 + β·d]", loc="left",
              fontsize=12.5, fontweight="bold", color="#1a5276")

y0, h = 0.30, 0.22
# extensions
ax1.add_patch(plt.Rectangle((CMIN, y0), P1 - CMIN, h, facecolor=EXT_COLOR, edgecolor="#333"))
ax1.add_patch(plt.Rectangle((P2, y0), CMAX - P2, h, facecolor=EXT_COLOR, edgecolor="#333"))
# inter-parent range
ax1.add_patch(plt.Rectangle((P1, y0), d, h, facecolor=RANGE_COLOR, edgecolor="#333"))
# parents
for p, lab in ((P1, "parent 1"), (P2, "parent 2")):
    ax1.plot([p, p], [y0 - 0.10, y0 + h + 0.10], color="#c0392b", lw=2.0)
    ax1.text(p, y0 + h + 0.16, lab, ha="center", va="bottom", fontsize=9, color="#c0392b")

def brace(x0, x1, y, text):
    ax1.annotate("", xy=(x0, y), xytext=(x1, y),
                 arrowprops=dict(arrowstyle="<->", color="#444"))
    ax1.text((x0 + x1) / 2, y - 0.11, text, ha="center", va="top", fontsize=9, color="#444")

brace(CMIN, P1, y0 - 0.04, "α·d")
brace(P1, P2, y0 - 0.04, "d")
brace(P2, CMAX, y0 - 0.04, "β·d")
ax1.text(UB + 0.1, y0 + h / 2, f"α={ALPHA}, β={BETA}", ha="right", va="center",
         fontsize=9, color="#555")

# ---------- Panel 2: offspring distribution ----------
bins = np.linspace(LB, UB, 121)
ax2.hist(offspring, bins=bins, density=True, color=RANGE_COLOR, alpha=0.75,
         edgecolor="none")
for p in (P1, P2):
    ax2.axvline(p, color="#c0392b", lw=1.6, ls=(0, (4, 3)))
ax2.axvline(CMIN, color="#444", lw=1.0)
ax2.axvline(CMAX, color="#444", lw=1.0)
ax2.text(CMIN, ax2.get_ylim()[1] * 0.96, " cMin", fontsize=8.5, va="top", color="#444")
ax2.text(CMAX, ax2.get_ylim()[1] * 0.96, "cMax ", fontsize=8.5, va="top", ha="right",
         color="#444")
ax2.set_yticks([])
ax2.set_ylabel("offspring density")
ax2.set_xlabel("variable value      (bounds [%g, %g])" % (LB, UB))
ax2.set_xlim(LB - 0.3, UB + 0.3)
ax2.spines[["top", "right"]].set_visible(False)
ax2.text(0.5, 0.86, "offspring are drawn uniformly across the interval",
         transform=ax2.transAxes, ha="center", fontsize=9, color="#555")

fig.suptitle("BLX-αβ – Blend Crossover", fontsize=16, fontweight="bold")
fig.text(0.5, 0.005, "Eshelman & Schaffer (1993) · real encoding · 2 parents → 2 children",
         ha="center", fontsize=9, color="#555")
fig.tight_layout(rect=(0, 0.03, 1, 0.96))

out = os.path.join(os.path.dirname(__file__), os.pardir,
                   "docs", "resources", "figures", "blx_alpha_beta_crossover.png")
fig.savefig(out, dpi=150, bbox_inches="tight")
print("saved:", out)
print("parents:", P1, P2, "alpha,beta:", ALPHA, BETA)
print("interval:", round(CMIN, 3), round(CMAX, 3))
