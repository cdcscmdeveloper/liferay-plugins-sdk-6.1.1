package my.ep.web.backingbean.ct;

public class xyz {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int n1 = 10, n2 = 10, n3 = 0;
		n2 = n1;
		n3 = n2;
		n1 = n3;
		System.out.println(n1);
		System.out.println(n2);
		System.out.println(n3);
		if (n1 > 0)
	         	System.out.println("x");
		else if ( n1 < 0)
		       System.out.println("y");
		else
		      System.out.println("z");
		}
	}
