// Name: Mac Hayter
// Student id: 8256068
//
// The Planting Synchronization Problem
//
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Planting 
{
	public static void main(String args[]) 
	{
		int i;
		// Create Student, TA, Professor threads
		TA ta = new TA();
		Professor prof = new Professor(ta);
		Student stdnt = new Student(ta);

		// Start the threads
		prof.start();
		ta.start();
		stdnt.start();

		// Wait for prof to call it quits
		try {prof.join();} catch(InterruptedException e) { }; 
		// Terminate the TA and Student Threads
		ta.interrupt();
		stdnt.interrupt();
	}   
}

class Student extends Thread
{
	TA ta;

	public Student(TA taThread)
	{
		ta = taThread;
	}

	public void run()
	{
		while(true)
		{
			
			try {
				ta.semStudent.acquire();
				// Can dig a hole - lets get the shovel
				
				ta.semShovel.acquire();
				System.out.println("Student: Got the shovel");
				
				try {sleep((int) (100*Math.random()));} catch (Exception e) { break;} // Time to fill hole
				ta.incrHoleDug();  // hole filled - increment the number	
				System.out.println("Student: Hole "+ta.getHoleDug()+" Dug");
				ta.semShovel.release();
				ta.holeExists.release();
				System.out.println("Student: Letting go of the shovel");
				if(isInterrupted()) break;
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			System.out.println("Student: Must wait for TA "+ta.getMAX()+" holes ahead");

		}
		System.out.println("Student is done");
	}
}

class TA extends Thread
{
	// Some variables to count number of holes dug and filled - the TA keeps track of things
	private int holeFilledNum=0;  // number of the hole filled
	private int holePlantedNum=0;  // number of the hole planted
	private int holeDugNum=0;     // number of hole dug
	private final int MAX=5;   // can only get 5 holes ahead

	// add semaphores - the professor lets the TA manage things.
	public Semaphore semStudent;
	public Semaphore planted;
	public Semaphore holeExists;
	public Semaphore semShovel;

	public int getMAX() { return(MAX); }
	public void incrHoleDug() { holeDugNum++; }
	public int getHoleDug() { return(holeDugNum); }
	public void incrHolePlanted() { holePlantedNum++; }
	public int getHolePlanted() { return(holePlantedNum); }
	public int getHoleFilled() { return(holeFilledNum); }

	public TA()
	{
		// Initialise things here
		semStudent = new Semaphore(MAX);
		planted	= new Semaphore(1);
		holeExists = new Semaphore(1);
		semShovel = new Semaphore(1);
		try {
			planted.acquire();
			holeExists.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void run()
	{
		while(true)
		{
			
			try {
				planted.acquire();
				semShovel.acquire();
				System.out.println("TA: Got the shovel");
				try {sleep((int) (100*Math.random()));} catch (Exception e) { break;} // Time to fill hole
				holeFilledNum++;  // hole filled - increment the number	
				System.out.println("TA: The hole "+holeFilledNum+" has been filled");
				System.out.println("TA: Letting go of the shovel");
				semShovel.release();
				semStudent.release();
				if(isInterrupted()) break;
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

			
			
			
		}
		System.out.println("TA is done");
	}
}

class Professor extends Thread
{
	TA ta;

	public Professor(TA taThread)
	{
		ta = taThread;
	}

	public void run()
	{
		
		while(ta.getHolePlanted() <= 20)
		{
			try {
				ta.holeExists.acquire();
				try {sleep((int) (50*Math.random()));} catch (Exception e) { break;} // Time to plant
				ta.incrHolePlanted();  // the seed is planted - increment the number	
				System.out.println("Professor: All be advised that I have completed planting hole "+
						ta.getHolePlanted());
				ta.planted.release();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		System.out.println("Professeur: We have worked enough for today");
		ta.holeExists.release();
		ta.planted.release();
		ta.semShovel.release();
		ta.semStudent.release();
	}
}
