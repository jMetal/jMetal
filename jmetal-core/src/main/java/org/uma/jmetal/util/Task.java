/**
 * 
 */
package jmetal.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;
import java.util.Iterator;
import java.lang.Comparable;

/**
 * This class represents tasks.
 * @author shambakey1
 */
public class Task implements Comparable<Task>{

	
	private int id; // Task identifier
	private int db_id;	//Task identifier according to tasks table database
	private double exec_time; // Execution time or number of instructions for
								// current task
	private Machine machine; // Current machine responsible of executing this task

	/**
	 * Contructor for Task
	 */
	public Task() {
		this.id = 0;
		this.exec_time = 0;
		machine = new Machine();
	}

	/**
	 * Constructor for Task
	 * @param id_in Task identifier
	 * @param exec_time_in Length of task
	 */
	public Task(int id_in, double exec_time_in) {
		id = id_in;
		exec_time = exec_time_in;
		machine = new Machine();
	}
	
	/**
	 * Constructor for Task
	 * @param id_in Identifier of task
	 * @param db_id_in Task identifier according to database. Not necessarily equals id_in
	 * @param exec_time_in Length of task
	 */
	public Task(int id_in, int db_id_in, double exec_time_in) {
		id = id_in;
		db_id=db_id_in;
		exec_time = exec_time_in;
		machine = new Machine();
	}

	/**
	 * Constructor for Task
	 * @param id_in Task identifier
	 * @param exec_time_in Task length
	 * @param m Machine executing this task
	 */
	public Task(int id_in, double exec_time_in, Machine m) {
		id = id_in;
		exec_time = exec_time_in;
		machine = m;
	}
	
	/**
	 * Constructor for Task
	 * @param id_in Task identifier
	 * @param db_id_in Task identifier according to database. Not necessarily equals id_in
	 * @param exec_time_in Task length
	 * @param m Machine executing this task
	 */
	public Task(int id_in,int db_id_in, double exec_time_in, Machine m) {
		id = id_in;
		db_id=db_id_in;
		exec_time = exec_time_in;
		machine = m;
	}

	/**
	 * Returns current assigned machine for this task
	 * @return the machine
	 */
	public Machine getMachine() {
		if(this.machine==null){
			System.out.println("Task "+this.id+" has not been assigned to any machine");
			System.exit(0);
		}
		return this.machine;
	}

	/**
	 * Sets machine m to current task
	 * @param m the machine to set
	 */
	public void setMachine(Machine m) {
		this.machine = m;
	}

	/**
	 * Get ID of current task
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets ID of current task
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns execution time of current task
	 * @return the exec_time
	 */
	public double getExec_time() {
		return exec_time;
	}

	/**
	 * Sets execution time for current task
	 * @param exec_time the exec_time to set
	 */
	public void setExec_time(double exec_time) {
		this.exec_time = exec_time;
	}

	/************************ Task comparators ****************************/
	
	/** 
	 * Default comparison between Task instances is based on execution time of each task.
	 * Tasks are ordered in non-ascending order
	 */
	@Override
	public int compareTo(Task t) {
		return (int)Math.signum(t.getExec_time()-this.exec_time);
	}
	
	/**
	 * Non descending order of tasks based on execution time
	 */
	public static Comparator<Task> ascExecComparator = new Comparator<Task>() {
		public int compare(Task t1, Task t2) {
			return (int) Math.signum(t1.getExec_time() - t2.getExec_time());
		}
	};
	
	/********************* Set of static operations on Tasks ***************************/

	/**
	 * Generates set of tasks based on required specifications
	 * @param task_num Required number of tasks
	 * @param task_id_start Starting ID of tasks. This is useful if tasks are generated at different intervals
	 * @param max_task_exec_time Maximum execution time for any task
	 * @param fixed If true, then all tasks have the same maximum execution time
	 * @param real If true, then execution time can have decimal fractions. This works only if fixed is false
	 * @return Set of required tasks
	 */
	public static Vector<Task> genTasks(int task_num, int task_id_start, double max_task_exec_time,
			boolean fixed, boolean real){
		Vector<Task> task_set=new Vector<Task>(task_num);
		Random r=new Random();
		double tmp_exec_time;
		if(fixed){
			for(int indx=task_id_start;indx<task_id_start+task_num;indx++){
				task_set.add(new Task(indx,max_task_exec_time));
			}
		}
		else{
			for(int indx=task_id_start;indx<task_id_start+task_num;indx++){
				if(real){
					while((tmp_exec_time=(double)(max_task_exec_time*r.nextDouble()))==0){
						//task execution time cannot be zero. So, loop until a non-zero execution time is achieved
					}
				}
				else{
					while((tmp_exec_time=Math.abs((r.nextLong()%max_task_exec_time)))==0){
						//task execution time cannot be zero. So, loop until a non-zero execution time is achieved
					}
				}
				task_set.add(new Task(indx,tmp_exec_time));
			}
		}
		return task_set;
	}
	
	/**
	 * Generates set of tasks by extracting them from specified database
	 * @param userName User name for connecting to the specified database
	 * @param password Password to connect for the specified database
	 * @param dbms Type of database management system (i.e., MYSQL or JDBS)
	 * @param serverName Name of the server hosting the database
	 * @param portNumber Port number of the server
	 * @param dbName Name of specified database
	 * @param dataset_conf_id Identifier of required dataset
	 * @return Set of tasks
	 */
	public static Vector<Task> genTasks(String userName,
			String password, String dbms, String serverName, int portNumber,
			String dbName, int dataset_conf_id) {
		return DBInOut.getTasks(userName, password, dbms, serverName, portNumber, dbName, dataset_conf_id);
	}
	
	/**
	 * Prints properties of current task
	 * @param taskSet Set of task to print their properties
	 * @param machine_in If true, then prints machine assigned for this task
	 */
	public static void printTasks(Vector<Task> taskSet,boolean machine_in){
		for(Task t:taskSet){
			System.out.println("Task_ID:"+t.getId()+" ,Task_exec_time:"+t.getExec_time());
			if(machine_in){
				Vector<Machine> m=new Vector<Machine>(1);
				m.add(t.getMachine());
				Machine.printMachines(m,false);
			}
			System.out.println();
		}
	}
	
	/**
	 * Extract IDs of a set of tasks
	 * @param taskSet_in The task set to extract their IDs
	 * @return Set of tasks' IDs
	 */
	public static Vector<Integer> getTasksIDs(Vector<Task> taskSet_in){
		Iterator<Task> t1=taskSet_in.iterator();
		Vector<Integer> tasksIDs=new Vector<Integer>(taskSet_in.size());
		while(t1.hasNext()){
			tasksIDs.add(t1.next().getId());
		}
		return tasksIDs;
	}
	
	/**
	 * Extract IDs of a set of tasks as a CSV string
	 * @param taskSet_in The task set to extract their IDs
	 * @return Set of tasks' IDs as 
	 */
	public static String getTasksIDsString(Vector<Task> taskSet_in){
		Iterator<Task> t1=taskSet_in.iterator();
		String tasksIDs="\"";
		while(t1.hasNext()){
			tasksIDs+=t1.next().getId();
			if(t1.hasNext()){
				tasksIDs+="_";
			}
		}
		tasksIDs+="\"";
		return tasksIDs;
	}
	
	/**
	 * Return a reference to task by ID
	 * @param taskSet Set of task containing the required task
	 * @param ID Identifier of the specified task
	 * @return Reference to the task with the specified ID
	 */
	public static Task getTaskbyID(Vector<Task> taskSet, int ID){
		Iterator<Task> it=taskSet.iterator();
		Task tmp_task=null;
		while(it.hasNext()){
			if((tmp_task=it.next()).getId()==ID){
				break;
			}
		}
		return tmp_task;
	}
	
	/**
	 * Extract task with minimum execution time from set of tasks
	 * @param taskSet Set of tasks to exctract the one with minimum execution time
	 * @return Task with minimum execution time
	 */
	public static Task getMinExecTask(Vector<Task> taskSet){
		return Collections.min(taskSet, Task.ascExecComparator);
	}
	
	/**
	 * Extracts machines' IDs of input task set
	 * @param taskSet Task set to extract their machine IDs 
	 * @return Machines' IDs on input task set
	 */
	public static int[] getMachineIDs(Vector<Task> taskSet){
		int[] IDs=new int[taskSet.size()];
		for(int i=0;i<IDs.length;i++){
			IDs[i]=Task.getTaskbyID(taskSet, i).getMachine().getID();
		}
		return IDs;
	}
}
