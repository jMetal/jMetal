"""Usage: python df_reference_values.py > jmetal-problem/src/test/resources/df/reference-values.csv

Reference values of DF1-DF14 from a transcription of the CEC 2018 technical report
(Jiang et al., 2017), checked against pymoo where pymoo follows the report."""
import sys
import numpy as np
from numpy import sin, cos, pi, floor, abs as nabs

def df(i, x, t):
    n = len(x); x1 = x[0]
    if i == 1:
        G = nabs(sin(.5*pi*t)); H = .75*sin(.5*pi*t)+1.25; g = 1+np.sum((x[1:]-G)**2)
        return [x1, g*(1-(x1/g)**H)]
    if i == 2:
        G = nabs(sin(.5*pi*t)); r = int(floor((n-1)*G))  # 0-based (report: 1 + floor)
        g = 1+np.sum((np.delete(x, r)-G)**2)
        return [x[r], g*(1-(x[r]/g)**.5)]
    if i == 3:
        G = sin(.5*pi*t); H = G+1.5; g = 1+np.sum((x[1:]-G-x1**H)**2)
        return [x1, g*(1-(x1/g)**H)]
    if i == 4:
        a = sin(.5*pi*t); b = 1+nabs(cos(.5*pi*t)); c = max(abs(a), a+b); H = 1.5+a
        idx = np.arange(2, n+1)
        g = 1+np.sum((x[1:]-a*(x1/c)**2/idx)**2)
        return [g*abs(x1-a)**H, g*abs(x1-a-b)**H]
    if i == 5:
        G = sin(.5*pi*t); w = floor(10*G); g = 1+np.sum((x[1:]-G)**2)
        return [g*(x1+.02*sin(w*pi*x1)), g*(1-x1+.02*sin(w*pi*x1))]
    if i == 6:
        G = sin(.5*pi*t); a = .2+2.8*abs(G); y = x[1:]-G
        g = 1+np.sum(abs(G)*y**2-10*cos(2*pi*y)+10)
        return [g*(x1+.1*sin(3*pi*x1))**a, g*(1-x1+.1*sin(3*pi*x1))**a]
    if i == 7:
        a = 5*cos(.5*pi*t); g = 1+np.sum((x[1:]-1/(1+np.exp(a*(x1-2.5))))**2)
        return [g*(1+t)/x1, g*x1/(1+t)]
    if i == 8:
        G = sin(.5*pi*t); a = 2.25+2*cos(2*pi*t); beta = 1
        g = 1+np.sum((x[1:]-G*sin(4*pi*x1**beta)/(1+abs(G)))**2)
        return [g*(x1+.1*sin(3*pi*x1)), g*(1-x1+.1*sin(3*pi*x1))**a]
    if i == 9:
        N = 1+floor(10*abs(sin(.5*pi*t))); g = 1
        for j in range(1, n):
            g += (x[j]-cos(4*t+x1+x[j-1]))**2
        m = max(0, (.1+.5/N)*sin(2*N*pi*x1))
        return [g*(x1+m), g*(1-x1+m)]
    x2 = x[1]
    if i == 10:
        G = sin(.5*pi*t); H = 2.25+2*cos(.5*pi*t)
        g = 1+np.sum((x[2:]-sin(2*pi*(x1+x2))/(1+abs(G)))**2)   # report: 2 pi
        return [g*sin(.5*pi*x1)**H, g*(sin(.5*pi*x2)*cos(.5*pi*x1))**H,
                g*(cos(.5*pi*x2)*cos(.5*pi*x1))**H]
    if i == 11:
        G = abs(sin(.5*pi*t)); g = 1+G+np.sum((x[2:]-.5*G*x1)**2)
        y1 = pi*G/6+(pi/2-pi*G/3)*x1; y2 = pi*G/6+(pi/2-pi*G/3)*x2
        return [g*sin(y1), g*sin(y2)*cos(y1), g*cos(y2)*cos(y1)]
    if i == 12:
        k = floor(10*sin(pi*t)); r = 1-np.mod(k, 2)                # report
        prod = sin(floor(k*(2*x1-r))*pi/2)*sin(floor(k*(2*x2-r))*pi/2)
        g = 1+np.sum((x[2:]-sin(t*x1))**2)+abs(prod)
        return [g*cos(.5*pi*x2)*cos(.5*pi*x1), g*sin(.5*pi*x2)*cos(.5*pi*x1), g*sin(.5*pi*x1)]
    if i == 13:
        G = sin(.5*pi*t); p = floor(6*G); g = 1+np.sum((x[2:]-G)**2)
        s = sum(sin(.5*pi*xj)**2+sin(.5*pi*xj)*cos(p*pi*xj)**2 for xj in (x1, x2))
        return [g*cos(.5*pi*x1)**2, g*cos(.5*pi*x2)**2, g*s]
    if i == 14:
        G = sin(.5*pi*t); g = 1+np.sum((x[2:]-G)**2); y = .5+G*(x1-.5)
        hy = y+.05*sin(6*pi*y)
        return [g*(1-y+.05*sin(6*pi*y)), g*(1-x2+.05*sin(6*pi*x2))*hy, g*(x2+.05*sin(6*pi*x2))*hy]

def bounds(i, n):
    lo = np.zeros(n); hi = np.ones(n)
    if i in (3,): lo[1:], hi[1:] = -1, 2
    if i == 4: lo[:], hi[:] = -2, 2
    if i in (5, 6, 8, 9): lo[1:], hi[1:] = -1, 1
    if i == 7: lo[0], hi[0] = 1, 4
    if i in (10, 12, 13, 14): lo[2:], hi[2:] = -1, 1
    return lo, hi

def main():
    try:
        import pymoo.problems.dynamic.df as pdf
    except ImportError:
        pdf = None
    rng = np.random.default_rng(2018)
    rows, maxdiff = [], 0.0
    taus = [0, 1, 2, 3, 5, 7, 10, 13, 20, 27]   # t = tau / 10 with tau_t = 1, n_t = 10
    for i in range(1, 15):
        for n in (10, 5):
            lo, hi = bounds(i, n)
            for tau in taus:
                t = tau / 10
                for _ in range(2):
                    x = lo + (hi-lo)*rng.random(n)
                    f = df(i, x, t)
                    if pdf is not None and i not in (10, 12):
                        p = getattr(pdf, f"DF{i}")(n_var=n); p.time = t
                        fp = p.evaluate(x.reshape(1, -1))[0]
                        maxdiff = max(maxdiff, np.max(np.abs(np.array(f)-fp)/np.maximum(1, np.abs(fp))))
                    rows.append([i, n, tau, *x, *f])
    print(f"# max relative difference with pymoo (DF1-9, DF11, DF13, DF14): {maxdiff:.2e}", file=sys.stderr)
    print("# DF reference values: problem, n, tau (t = tau / 10), the n variables, the objectives.")
    print("# Transcription of Jiang et al., CEC 2018 report (df_reference_values.py); equal to pymoo")
    print("# except in DF10 (2 pi) and DF12 (k = floor(10 sin(pi t)), r = 1 - mod(k, 2)).")
    for r in rows:
        print(",".join(str(int(v)) if k < 3 else repr(float(v)) for k, v in enumerate(r)))

main()
