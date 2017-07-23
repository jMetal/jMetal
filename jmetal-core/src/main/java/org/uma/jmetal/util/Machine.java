package jmetal.util;

import java.util.Collections;
import java.util.Vector;
import java.util.Comparator;
import java.util.Random;
import java.util.Iterator;

/**
 * This class represents computing elements such as machines, processors, nodes.
 * It is useful for scheduling problems like jobshop and flowshop
 * 
 * @author shambakey1
 */
public class Machine {
	/*
	 * Original members that describes this machine
	 */
	private int id; // Machine identifier
	private int db_id; // Machine identifier recorded in database_conf table
	private long speed; // Speed of current machine
	private Vector<Task> ass_tasks; // Assigned tasks to this machine

	/*
	 * Metric members that are used to measure performance of this machine
	 * (i.e., span)
	 */
	private double span; // Final span of this machine due to assigned tasks.

	/**
	 * Default constructor. ID, speed and span are set to 0 which is initial
	 * invalid value.
	 */
	public Machine() {
		this.id = 0;
		this.speed = 0;
		this.span = 0;
		ass_tasks = new Vector<Task>();
	}

	/**
	 * Constructor with identifier and speed of current machine.
	 * 
	 * @param id_in
	 *            Identifier for current machine
	 * @param speed_in
	 *            Speed of current machine
	 */
	public Machine(int id_in, long speed_in) {
		this();
		this.id = id_in;
		this.speed = speed_in;
	}

	/**
	 * Constructor with identifier, database identifier and speed of current
	 * machine
	 * 
	 * @param id_in
	 *            Identifier of current machine
	 * @param db_id_in
	 *            Identifier of current machine according to database which not
	 *            necessarily equals identifier
	 * @param speed_in
	 *            Speed of current machine
	 */
	public Machine(int id_in, int db_id_in, long speed_in) {
		this();
		this.id = id_in;
		this.db_id = db_id_in;
		this.speed = speed_in;
	}

	/**
	 * Constructor with identifier, speed and assigned tasks for current
	 * machine.
	 * 
	 * @param id_in
	 *            Identifier of current machine
	 * @param speed_in
	 *            Speed of current machine
	 * @param ass_tasks_in
	 *            Assigned tasks for current machine
	 */
	public Machine(int id_in, long speed_in, Vector<Task> ass_tasks_in) {
		this(id_in, speed_in);
		this.ass_tasks.addAll(ass_tasks_in);
	}

	/**
	 * Constructor with identifier, identifier according to dataset_conf table,
	 * speed and assigned tasks for current machine.
	 * 
	 * @param id_in
	 *            Identifier of current machine
	 * @param db_id_in
	 *            Identifier of current machine according to database which not
	 *            necessarily equals identifier
	 * @param speed_in
	 *            Speed of current machine
	 * @param ass_tasks_in
	 *            Assigned tasks for current machine
	 */
	public Machine(int id_in, int db_id_in, long speed_in,
			Vector<Task> ass_tasks_in) {
		this(id_in, db_id_in, speed_in);
		this.ass_tasks.addAll(ass_tasks_in);
	}

	/**
	 * Sets identifier for current machine
	 * 
	 * @param id_in
	 *            Identifier for current machine
	 */
	public void setID(int id_in) {
		this.id = id_in;
	}

	/**
	 * Print ID of current machine
	 * 
	 * @return ID of current machine
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Returns speed of current machine. Speed can be, but not limited to,
	 * million instructions per second
	 * 
	 * @return Speed of current machine
	 */
	public long getSpeed() {
		return this.speed;
	}

	/**
	 * Sets speed for current machine. Speed can be, but not limited to, million
	 * instructions per second
	 * 
	 * @param speed
	 */
	public void setSpeed(long speed) {
		this.speed = speed;
	}

	/**
	 * Return assigned tasks for current machine
	 * 
	 * @return Assigned tasks for current machine
	 */
	public Vector<Task> getTasks() {
		return ass_tasks;
	}

	/**
	 * Assigns tasks for current machine
	 * 
	 * @param tasks
	 *            Set of tasks to be assigned for current machine
	 */
	public void setTasks(Vector<Task> tasks) {
		this.ass_tasks.addAll(tasks);
	}

	/**
	 * Appends task t at the end of list of tasks to this machine. Span is
	 * changed accordingly. Current machine is recorded as the machine {@code t}'s fields
	 * @param t
	 *            Task to be appended to list of tasks assigned to this machine
	 */
	public void addTask(Task t) {
		this.ass_tasks.add(t);
		this.span += t.getExec_time() / this.speed;
		t.setMachine(this);
	}

	/**
	 * Adds task t to the current vector of tasks to this machine at the
	 * specified index. Span is changed accordingly. Current machine is recorded as the machine {@code t}'s fields
	 * 
	 * @param t
	 *            Task to be added
	 * @param indx
	 *            Index of the to be assigned task
	 */
	public void addTask(Task t, int indx) {
		this.ass_tasks.add(indx, t);
		this.span += t.getExec_time() / this.speed;
		t.setMachine(this);
	}

	/**
	 * Append a collection of tasks to the end of assigned tasks to this
	 * machine. Span is changed accordingly. Current machine is recorded as the machine {@code t}'s fields
	 * 
	 * @param t
	 *            Set of tasks to be added to this machine. These tasks are
	 *            added to the end of the task list
	 */
	public void addTasks(Vector<Task> t) {
		this.ass_tasks.addAll(t);
		Iterator<Task> it = t.iterator();
		Task tmp_task;
		while (it.hasNext()) {
			tmp_task=it.next();
			this.span += tmp_task.getExec_time() / this.speed;
			tmp_task.setMachine(this);
		}
	}

	/**
	 * Remove specified task (if exists) from current machine. Span is modified
	 * accordingly
	 * 
	 * @param t
	 *            Task to be removed from current assigned task to current
	 *            machine
	 */
	public void removeTask(Task t) {
		if (this.ass_tasks.removeElement(t)) {
			this.span -= t.getExec_time() / this.speed;
		}
	}

	/**
	 * Removes all tasks and span assigned to this machine.
	 * 
	 * @param ID
	 *            If true, reset ID of this machine to its un-itialized value.
	 *            Otherwise, keep dataset_conf_iter for further use.
	 */
	public void clearSpanAssTasks(boolean ID) {
		this.ass_tasks.clear();
		this.span = 0;
		if (ID) {
			this.id = 0;
		}
	}

	/**
	 * Removes only assigned tasks to current machine. Other things (like span)
	 * are left intact
	 * 
	 * @param ID
	 */
	public void clearTasks(boolean ID) {
		this.ass_tasks.clear();
		if (ID) {
			this.id = 0;
		}
	}

	/**
	 * Returns span value of current machine.
	 * 
	 * @return Span of this machine
	 */
	public double getSpan() {
		return this.span;
	}

	/**
	 * Sets span for this machine
	 * 
	 * @param span_in
	 *            Span value to be assigned for current machine.
	 */
	public void setSpan(double span_in) {
		this.span = span_in;
	}

	/**
	 * Calculates span of current machine based on assigned tasks and machine
	 * speed
	 * 
	 * @param set_span
	 *            If true, sets span of current machine to the calculated one
	 * @return Calculated span
	 */
	public double calcSpan(boolean set_span) {
		double calc_span = 0; // Holds calculated span
		if (this.ass_tasks.isEmpty()) {
			calc_span = 0;
		} else {
			for (Task t : this.ass_tasks) {
				calc_span += t.getExec_time() / this.speed;
			}
		}
		if (set_span) {
			this.span = calc_span;
		}
		return calc_span;
	}

	/**
	 * Reset span of current machine to un-initialized value
	 */
	public void resetSpan() {
		this.span = 0;
	}

	/********************* Set of static operations on Machines ***************************/

	/**
	 * Generates required machines
	 * 
	 * @param machine_num
	 *            Number of computing elements
	 * @param machine_id_start
	 *            Starting ID for generated set of machines
	 * @param max_machine_speed
	 *            Maximum speed of any computing element
	 * @param fixed
	 *            If true, then all machines have the same speed which is
	 *            max_machine_speed
	 * @return Set of machines with required specifications
	 */
	public static Vector<Machine> genMachines(int machine_num,
			int machine_id_start, long max_machine_speed, boolean fixed) {
		Vector<Machine> machine_set = new Vector<Machine>(machine_num);
		if (fixed) {
			for (int indx = machine_id_start; indx < machine_id_start
					+ machine_num; indx++) {
				machine_set.add(new Machine(indx, max_machine_speed));
			}
		} else {
			Random r = new Random();
			long tmp_speed;
			for (int indx = machine_id_start; indx < machine_id_start
					+ machine_num; indx++) {
				while ((tmp_speed = Math.abs(r.nextLong() % max_machine_speed)) == 0) {
					// machine speed cannot be zero. So, loop until a non-zero
					// speed is achieved
				}
				machine_set.add(new Machine(indx, tmp_speed));
			}
		}
		return machine_set;
	}

	/**
	 * Generates set of machines by extracting them from specified database
	 * 
	 * @param userName
	 *            User name for connecting to the specified database
	 * @param password
	 *            Password to connect for the specified database
	 * @param dbms
	 *            Type of database management system (i.e., MYSQL or JDBS)
	 * @param serverName
	 *            Name of the server hosting the database
	 * @param portNumber
	 *            Port number of the server
	 * @param dbName
	 *            Name of specified database
	 * @param dataset_conf_id
	 *            Identifier of required dataset
	 * @return Set of machines
	 */
	public static Vector<Machine> genMachines(String userName, String password,
			String dbms, String serverName, int portNumber, String dbName,
			int dataset_conf_id) {
		return DBInOut.getMachines(userName, password, dbms, serverName,
				portNumber, dbName, dataset_conf_id);
	}

	/**
	 * Print machines' properties
	 * 
	 * @param machineSet
	 *            Set of machines to print their properties
	 * @param tasks_in
	 *            If true, prints properties of assigned tasks to each machine
	 */
	public static void printMachines(Vector<Machine> machineSet,
			boolean tasks_in) {
		for (Machine m : machineSet) {
			System.out.println("Machine_ID:" + m.getID() + ", Machine_speed:"
					+ m.getSpeed() + " ,Machine_span:" + m.getSpan());
			if (tasks_in) {
				Task.printTasks(m.getTasks(), false);
			}
			System.out.println();
		}
	}

	/**
	 * Print results of mypso_algorithm using machine set to database
	 * 
	 * @param alg_name_table
	 *            Table name
	 * @param machineSet
	 * @param dataset_conf_id
	 * @param iter_no
	 * @param userName
	 * @param password
	 * @param dbms
	 * @param serverName
	 * @param portNumber
	 * @param dbName
	 */
	public static void printAlg(String alg_name_table,
			Vector<Machine> machineSet, int dataset_conf_id, int iter_no,
			String userName, String password, String dbms, String serverName,
			int portNumber, String dbName) {
		DBInOut.printAlg(alg_name_table, machineSet, dataset_conf_id, iter_no,
				userName, password, dbms, serverName, portNumber, dbName);
	}

	/**
	 * Extracts maximum span among input machine set
	 * 
	 * @param machineset
	 *            Set of machines to get maximum span among them
	 * @return Maximum span among input machine set
	 */
	public static double getMaxSpan(Vector<Machine> machineset) {
		return Machine.getMaxSpanMachine(machineset).getSpan();
	}

	/**
	 * Extracts machine with maximum span
	 * 
	 * @param machineset
	 *            Set of machines to extract the one with maximum span
	 * @return Machine with maximum span
	 */
	public static Machine getMaxSpanMachine(Vector<Machine> machineset) {
		return Collections.max(machineset, Machine.ascSpanComparator);
	}

	/**
	 * Debug method for {@link getMaxSpanMachine(Vector<Machine> machineset)}
	 * 
	 * @param machineset
	 *            Set of machines to extract the one with maximum span
	 * @return Machine with maximum span
	 */
	public static Machine getMaxSpanMachineDebug(Vector<Machine> machineset) {
		Machine req_m = null;
		double cur_span = 0; // Holds current span for each machine
		double max_span = 0; // Holds maximum span up to now
		Iterator<Task> task_it; // Iterator for set of tasks assigned to each
								// machine
		Task t = null; // Current task
		for (Machine m : machineset) {
			cur_span = 0; // Reset span for current machine
			task_it = m.getTasks().iterator();
			/*
			 * Iterate through tasks of current machine
			 */
			while (task_it.hasNext()) {
				t = task_it.next();
				cur_span += t.getExec_time() / m.getSpeed();
			}
			/*
			 * Check if currently calculated span equals recorded span
			 */
			if (cur_span != m.getSpan()) {
				System.out.println("Machine " + m.getID() + ", rec_span: "
						+ m.getSpan() + ", calc_span: " + cur_span);
			}
			/*
			 * Check maximum span found so far and modify it (with associated
			 * machine) accordingly
			 */
			if (cur_span > max_span) {
				max_span = cur_span;
				req_m = m;
			}
		}
		/*
		 * Check the original getMaxSpanMachine is working correctly
		 */
		if (max_span != Machine.getMaxSpanMachine(machineset).getSpan()) {
			System.out.println("Calc_max_span: " + max_span
					+ ", Calc_max_span_mach: " + req_m.getID()
					+ ", rec_max_span: " + Machine.getMaxSpan(machineset)
					+ ", rec_max_span_machine: "
					+ Machine.getMaxSpanMachine(machineset).getID());
		}
		return req_m;
	}

	/**
	 * Extracts minimum span among input machine set
	 * 
	 * @param machineset
	 *            Set of machines to get minimum span among them
	 * @return minimum span among input machine set
	 */
	public static double getMinSpan(Vector<Machine> machineset) {
		return Collections.min(machineset, Machine.ascSpanComparator).getSpan();
	}

	/**
	 * Extracts machine with maximum span
	 * 
	 * @param machineset
	 *            Set of machines to extract the one with maximum span
	 * @return Machine with maximum span
	 */
	public static Machine getMinSpanMachine(Vector<Machine> machineset) {
		return Collections.min(machineset, Machine.ascSpanComparator);
	}

	/**
	 * Return minimum ID of any machine in the specified machine set
	 * 
	 * @param machineset
	 *            Set of machines to be investigated
	 * @return Minimum ID of any machine in the specified machine set
	 */
	public static int getMinMachineID(Vector<Machine> machineset) {
		int min_id = 0;
		if (!machineset.isEmpty()) {
			min_id = machineset.get(0).getID();
		} else {
			System.out
					.println("Cannot get minimum ID of machines because there are no machines");
			System.exit(0);
		}
		for (Machine m : machineset) {
			if (m.getID() < min_id) {
				min_id = m.getID();
			}
		}
		return min_id;
	}

	/**
	 * Return maximum ID of any machine in the specified machine set
	 * 
	 * @param machineset
	 *            Set of machines to be investigated
	 * @return Maximum ID of any machine in the specified machine set
	 */
	public static int getMaxMachineID(Vector<Machine> machineset) {
		int max_id = 0;
		if (!machineset.isEmpty()) {
			max_id = machineset.get(0).getID();
		} else {
			System.out
					.println("Cannot get minimum ID of machines because there are no machines");
			System.exit(0);
		}
		for (Machine m : machineset) {
			if (m.getID() > max_id) {
				max_id = m.getID();
			}
		}
		return max_id;
	}

	/**
	 * Reset span of all machines. Note that dataset_conf_iter does not change
	 * anything else (i.e., dataset_conf_iter does not clear set of assigned
	 * tasks to any machine)
	 * 
	 * @param machineSet
	 *            Set of machines to reset their span
	 */
	public static void clearSpan(Vector<Machine> machineSet) {
		Iterator<Machine> it = machineSet.iterator();
		while (it.hasNext()) {
			it.next().setSpan(0);
		}
	}

	/**
	 * Remove all tasks assigned to all machines in the specified machine set
	 * and reset its span
	 * 
	 * @param machineSet
	 *            Set of machines to completely remove tasks assigned to them
	 */
	public static void clearSpanAssTasks(Vector<Machine> machineSet) {
		Iterator<Machine> it = machineSet.iterator();
		while (it.hasNext()) {
			it.next().clearSpanAssTasks(false);
			;
		}
	}

	/**
	 * Return the machine with the specified ID
	 * 
	 * @param machineSet
	 *            Set of machines containing the machine with required ID
	 * @param id
	 *            ID of the machine to be extracted
	 * @return The machine with specified ID
	 */
	public static Machine getMachinebyID(Vector<Machine> machineSet, int ID) {
		Machine tmp_machine = null;
		Iterator<Machine> it = machineSet.iterator();
		while (it.hasNext()) {
			if ((tmp_machine = it.next()).getID() == ID) {
				break;
			}
		}
		return tmp_machine;
	}

	/**
	 * Extract IDs of all machines in the specified set
	 * 
	 * @param machineSet
	 *            The set of machines to extract their IDs
	 * @return IDs of all machines in the specified set
	 */
	public static Vector<Integer> getMachinesIDs(Vector<Machine> machineSet) {
		Vector<Integer> IDs = new Vector<Integer>(machineSet.size());
		Iterator<Machine> it = machineSet.iterator();
		while (it.hasNext()) {
			IDs.add(new Integer(it.next().getID()));
		}
		return IDs;
	}

	/************************** Set of comparators for the Machine class **********************************/
	/**
	 * Non ascending order of machines based on span
	 */
	public static Comparator<Machine> descSpanComparator = new Comparator<Machine>() {
		public int compare(Machine m1, Machine m2) {
			return (int) Math.signum(m2.getSpan() - m1.getSpan());
		}
	};

	/**
	 * Non descending order of machines based on span
	 */
	public static Comparator<Machine> ascSpanComparator = new Comparator<Machine>() {
		public int compare(Machine m1, Machine m2) {
			return (int) Math.signum(m1.getSpan() - m2.getSpan());
		}
	};

	/**
	 * Non descending order of machines based on ID
	 */
	public static Comparator<Machine> ascMachineID = new Comparator<Machine>() {
		public int compare(Machine m1, Machine m2) {
			return (int) Math.signum(m1.getID() - m2.getID());
		}
	};

	/**
	 * Non ascending order of machines based on speed
	 */
	public static Comparator<Machine> descSpeedComparator = new Comparator<Machine>() {
		public int compare(Machine m1, Machine m2) {
			return (int) Math.signum(m2.getSpeed() - m1.getSpeed());
		}
	};

	/**
	 * Non descending order of machines based on speed
	 */
	public static Comparator<Machine> ascSpeedComparator = new Comparator<Machine>() {
		public int compare(Machine m1, Machine m2) {
			return (int) Math.signum(m1.getSpeed() - m2.getSpeed());
		}
	};

	/**
	 * Generates the tasks' assignment as a vector of machines. Each Machine in
	 * the returned vector has its own assigned tasks. It also prints ID of
	 * machine with maximum span, as well as, maximum span value.
	 * 
	 * @param tasks
	 *            Set of tasks to be assigned on different machines
	 * @param solu
	 *            A solution offered by Single Objective PSO. This solution has
	 *            a pre-assigned span for each machine
	 * @return Set of machines with their assigned tasks and span of each
	 *         machine
	 */
	public static Vector<Machine> preSpantaskAssignment(Vector<Task> tasks,
			Vector<Machine> solu) {
		/*
		 * Check existence of tasks and resources
		 */
		if (tasks.isEmpty() || solu.isEmpty()) {
			System.out.println("No tasks and/or resources are specified");
			System.exit(0);
		}
		/*
		 * Organize tasks in non-ascending order of execution time
		 */
		Collections.sort(tasks);
		/*
		 * Organize machines in non-ascending order of pre-specified span given
		 * by PSO mypso_algorithm
		 */
		Collections.sort(solu, Machine.descSpeedComparator);
		/*
		 * Iterate through machines in "solu". Assign tasks to each machine
		 * according to pre-specified span
		 */
		Iterator<Machine> solu_it = solu.iterator(); // Iterator of machines in
														// solu
		Iterator<Task> task_it; // Iterator through currently unassigned tasks
		Machine tmp_machine = null; // Holds current machine in solu
		Machine tmp_lspan_machine = null; // Holds machine with currently least
											// span
		double tmp_span; // Holds pre-specified span of tmp_machine
		double calc_span; // To be assigned span for each machine
		double task_span_contr; // Contribution of task to the span of machine
		double tmp_max_span; // Temporary holder for maximum span
		Task tmp_task; // Holds current task in tasks
		/*
		 * Iterating through all machines except the last one
		 */
		while (solu_it.hasNext() && !tasks.isEmpty()) {
			/*
			 * Initialize temporary variables
			 */
			tmp_machine = solu_it.next();
			/*
			 * Initially, assign tasks to machines according to the fair share
			 * load
			 */
			tmp_span = tmp_machine.getSpan();
			tmp_machine.clearSpanAssTasks(false);
			calc_span = 0;
			task_it = tasks.iterator();

			/*
			 * Iterating through tasks. Note that tasks have already been
			 * ordered in non-ascending order
			 */
			while (task_it.hasNext()) {
				tmp_task = task_it.next();
				/*
				 * If remaining span of current machine accommodates execution
				 * time of current task, then assign current task to this
				 * machine
				 */
				if (tmp_task.getExec_time() <= tmp_span
						* (double) (tmp_machine.getSpeed())) {
					tmp_machine.addTask(tmp_task);
					/*
					 * Modify remaining span and calculated span of current
					 * machine
					 */
					task_span_contr = tmp_task.getExec_time()
							/ (double) (tmp_machine.getSpeed());
					tmp_span -= task_span_contr;
					/*
					 * Remove current task from the set of tasks
					 */
					task_it.remove();
					/*
					 * If current machine has exhausted its pre-assigned span,
					 * then no need to investigate other tasks
					 */
					if (tmp_span == 0) {
						break;
					}
				}
			}
		}
		/*
		 * If some tasks still exist, then traverse tasks in decreasing order of
		 * length. Assign each task to the machine that will have the least
		 * increase on total maximum span TODO: Other methods can be used to
		 * assign remaining tasks. I've tried assignment of all remaining tasks
		 * to the last machine, but it had very bad results
		 */
		if (!tasks.isEmpty()) {
			task_it = tasks.iterator();
			tmp_max_span = 0;
			/*
			 * Traverse through remaining tasks. Tasks are already order in
			 * descending order of length
			 */
			while (task_it.hasNext()) {
				tmp_task = task_it.next();
				/*
				 * Traverse through all machines. Machines are already sorted in
				 * descending order of speed
				 */
				solu_it = solu.iterator();
				while (solu_it.hasNext()) {
					tmp_machine = solu_it.next();
					/*
					 * Calculate effect on maximum span due to assignment of
					 * current task to current machine
					 */
					task_span_contr = tmp_task.getExec_time()
							/ tmp_machine.getSpeed() + tmp_machine.getSpan();
					if (tmp_machine == solu.firstElement()) {
						tmp_max_span = task_span_contr;
						tmp_lspan_machine = tmp_machine;
					} else if (task_span_contr < tmp_max_span) {
						tmp_max_span = task_span_contr;
						tmp_lspan_machine = tmp_machine;
					}
				}
				tmp_lspan_machine.addTask(tmp_task);
			}
		}
		/*
		 * Finally, return solu as the solution vector
		 */
		return solu;
	}

	/**
	 * Works the same as {@link #reSpantaskAssignment(Vector<Task>
	 * tasks,Vector<Machine> solu)} except that it makes an internal copy of the
	 * input parameters such than no modification is made to the original input
	 * parameters
	 */
	public static Vector<Machine> preSpantaskAssignmentLocal(
			Vector<Task> tasks, Vector<Machine> solu) {
		Vector<Task> tmptasks = (Vector<Task>) tasks.clone();
		Vector<Machine> tmpmachine = (Vector<Machine>) solu.clone();
		Machine.preSpantaskAssignment(tmptasks, tmpmachine);
		return tmpmachine;
	}
}