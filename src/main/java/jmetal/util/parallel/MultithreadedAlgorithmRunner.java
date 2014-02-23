package jmetal.util.parallel;

import jmetal.core.Algorithm;
import jmetal.core.SolutionSet;
import jmetal.experiments.Experiment2;
import jmetal.experiments.Settings;
import jmetal.experiments.SettingsFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Antonio J. Nebro on 09/02/14.
 */
public class MultithreadedAlgorithmRunner extends SynchronousParallelRunner {
  private Collection<EvaluationTask> taskList_ ;
  private Experiment2 experiment_ ;

  public MultithreadedAlgorithmRunner(int threads) {
    super(threads) ;
  }

  public void startParallelRunner(Object experiment) {
    experiment_ = (Experiment2) experiment ;
    executor_ = Executors.newFixedThreadPool(numberOfThreads_) ;
    System.out.println("Cores: "+ numberOfThreads_) ;
    taskList_ = null ;
  }

  public void addTaskForExecution(Object[] taskParameters) {
    if (taskList_ == null)
      taskList_ = new ArrayList<EvaluationTask>();

    String algorithm = (String)taskParameters[0] ;
    String problem = (String)taskParameters[1];
    Integer id = (Integer)taskParameters[2] ;
    taskList_.add(new EvaluationTask(algorithm, problem, id)) ;
  }

  public Object parallelExecution() {
    List<Future<Object>> future = null ;
    try {
      future = executor_.invokeAll(taskList_);
    } catch (InterruptedException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    List<Object> resultList = new Vector<Object>() ;

    for(Future<Object> result : future){
      Object returnValue = null ;
      try {
        returnValue = result.get();
        resultList.add(returnValue) ;
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    taskList_ = null ;
    return null ;
  }

  public void stopParallelRunner() {
    executor_.shutdown() ;
  }

  private class EvaluationTask extends ParallelTask {
    private String problemName_ ;
    private String algorithmName_ ;
    private int id_ ;

    /**
     * Constructor
     * @param problem Problem to solve
     */
    public EvaluationTask(String algorithm,  String problem, int id) {
      problemName_ = problem ;
      algorithmName_ = algorithm ;
      id_ = id ;
    }

    public Integer call() throws Exception {
      Algorithm algorithm ;
      Object [] settingsParams = {problemName_} ;
      Settings settings  ; //= (new SettingsFactory()).getSettingsObject(algorithmName_, settingsParams) ;

      if (experiment_.useConfigurationFilesForAlgorithms_) {
        Properties configuration = new Properties();
        InputStreamReader isr = new InputStreamReader(new FileInputStream(algorithmName_+".conf"));
        configuration.load(isr);

        String algorithmName = configuration.getProperty("algorithm", algorithmName_) ;

        settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams) ;
        algorithm = settings.configure(configuration);
        isr.close();
      }
      else {
        settings = (new SettingsFactory()).getSettingsObject(algorithmName_, settingsParams) ;
        algorithm = settings.configure();
      }

      System.out.println(" Running algorithm: " + algorithmName_ + ", problem: " + problemName_ + ", run: " + id_);

      SolutionSet resultFront = algorithm.execute() ;

      File experimentDirectory;
      String directory;

      directory = experiment_.experimentBaseDirectory_ + "/data/" + algorithmName_ + "/" + problemName_ ;

      experimentDirectory = new File(directory);
      if (!experimentDirectory.exists()) {
        boolean result = new File(directory).mkdirs();
        System.out.println("Creating " + directory);
      }

      resultFront.printObjectivesToFile(directory + "/" + experiment_.outputParetoFrontFile_ + "." + id_);
      resultFront.printVariablesToFile(directory + "/" + experiment_.outputParetoSetFile_ + "." + id_);

      return id_ ;
    }
  }
}
