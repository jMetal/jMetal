"""Generate a diagram illustrating polynomial mutation on a real variable.

The sampling replicates PolynomialMutation.doMutation
(jmetal-core .../operator/mutation/impl/PolynomialMutation.java) for a single variable:
for a uniform random value u the perturbation deltaq is derived from the distribution
index and added to the variable. Sampling many u values shows the distribution of the
mutated value around the original one, which gets tighter as the distribution index grows.
"""
import os
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt


def polynomial_mutation(x, yl, yu, eta, u):
    """Vectorised polynomial mutation for one variable; returns mutated values for draws u."""
    span = yu - yl
    delta1 = (x - yl) / span
    delta2 = (yu - x) / span
    mut_pow = 1.0 / (eta + 1.0)

    xy_lo = 1.0 - delta1
    val_lo = 2.0 * u + (1.0 - 2.0 * u) * (xy_lo ** (eta + 1.0))
    deltaq_lo = val_lo ** mut_pow - 1.0

    xy_hi = 1.0 - delta2
    val_hi = 2.0 * (1.0 - u) + 2.0 * (u - 0.5) * (xy_hi ** (eta + 1.0))
    deltaq_hi = 1.0 - val_hi ** mut_pow

    deltaq = np.where(u <= 0.5, deltaq_lo, deltaq_hi)
    y = x + deltaq * span
    return np.clip(y, yl, yu)


X = 5.0
LB, UB = 0.0, 10.0
N = 400_000
rng = np.random.default_rng(7)
u = rng.random(N)

fig, axes = plt.subplots(2, 1, figsize=(10, 6.2), sharex=True)
bins = np.linspace(LB, UB, 161)

for ax, eta in ((axes[0], 20.0), (axes[1], 2.0)):
    y = polynomial_mutation(X, LB, UB, eta, u)
    ax.hist(y, bins=bins, density=True, color="#41ab5d", alpha=0.65, edgecolor="none")
    ax.axvline(X, color="#c0392b", lw=1.8, ls=(0, (4, 3)))
    ax.text(X, ax.get_ylim()[1] * 0.92, " original value x", color="#c0392b",
            fontsize=9, va="top")
    ax.set_ylabel("mutated-value density")
    ax.text(0.015, 0.86, f"distribution index  η = {eta:g}", transform=ax.transAxes,
            fontsize=11, fontweight="bold", color="#1a5276")
    ax.set_yticks([])
    ax.spines[["top", "right"]].set_visible(False)

axes[0].text(0.99, 0.86, "large η → small perturbations",
             transform=axes[0].transAxes, ha="right", fontsize=9, color="#555")
axes[1].text(0.99, 0.86, "small η → larger perturbations",
             transform=axes[1].transAxes, ha="right", fontsize=9, color="#555")
axes[1].set_xlabel("variable value      (bounds [%g, %g])" % (LB, UB))

fig.suptitle("Polynomial Mutation", fontsize=16, fontweight="bold")
fig.text(0.5, 0.005,
         "Deb & Goyal (1996) · real encoding · perturbation distribution sampled "
         "from the operator",
         ha="center", fontsize=9, color="#555")
fig.tight_layout(rect=(0, 0.03, 1, 0.97))

out = os.path.join(os.path.dirname(__file__), os.pardir,
                   "docs", "resources", "figures", "polynomial_mutation.png")
fig.savefig(out, dpi=150, bbox_inches="tight")
print("saved:", out)
print("x:", X, "bounds:", (LB, UB))
