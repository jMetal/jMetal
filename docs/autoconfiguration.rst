Autoconfiguration of evolutionary algorithms: NSGA-II
=====================================================

Before reading this section, readers are referred to the paper "Automatic configuration of NSGA-II with jMetal and irace", presented in GECCO 2019 (DOI: ), as we focus here mainly on implementation issues. The motivation for including auto configuration (or auto tuning) of multi-objecive evolutionary algorithms in jMetal, the proposed architecture, the aspects that can be tuned in NSGA-II, and the results of an experiment showing a use case are described in that paper.





API
---


.. code-block:: java

   public interface Algorithm<Result> extends Runnable, Serializable, DescribedEntity {
      void run() ;
      Result getResult() ;
   }




+------------+------------+-----------+
| Header 1   | Header 2   | Header 3  |
+============+============+===========+
| body row 1 | column 2   | column 3  |
+------------+------------+-----------+
| body row 2 | Cells may span columns.|
+------------+------------+-----------+
| body row 3 | Cells may  | - Cells   |
+------------+ span rows. | - contain |
| body row 4 |            | - blocks. |
+------------+------------+-----------+
