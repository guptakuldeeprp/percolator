
import edu.princeton.cs.algs4.QuickFindUF;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation
{

	private int n;
	WeightedQuickUnionUF unionFind;
	int[] sites;
	int[] virtualEnds = new int[2];
	boolean percolates = false;
	private static final int OPEN = 2;
	private static final int CLOSED = 0;
	private static final int CONNECTED_TOP = 4;
	private static final int CONNECTED_BOTTOM = 8;

	// create n-by-n grid, with all sites blocked
	public Percolation(int n)
	{
		if (n <= 0) throw new IllegalArgumentException("n must be positive");
		this.n = n;

		unionFind = new WeightedQuickUnionUF(n * n + 2); // because we support
														 // indices from 1 to
														 // n

		sites = new int[n * n + 2];

		// connect the virtual start with each site of the first row
		// for (int i = 1; i <= n; i++)
		// unionFind.union(0, i);

		// connect the virtual end with each site of the last row
		// for (int i = n * (n - 1) + 1; i <= n * n; i++)
		// unionFind.union(n * n + 1, i);

		// mark all real sites as closed
		for (int i = 1; i <= n * n; i++)
		{
			sites[i] = CLOSED;
		}

		// mark the virtual start and virtual end as open
		sites[0] = -1;
		sites[n * n + 1] = -1;

		System.out.println("initialized sites: ");
		print(sites);
	}

	private void print(int[] sites)
	{
		System.out.println(sites[0]);
		int i = 1;
		for (; i < sites.length - 1; i++)
		{
			System.out.print(sites[i] + ", ");
			if (i % n == 0) System.out.println();
		}
		System.out.println(sites[i]);
	}

	// open site (row i, column j) if it is not
	// open already
	public void open(int i, int j)
	{
		validateAndFail(i, j);

		int site1DIndex = xyTo1D(i, j);
		// sites[site1DIndex] = site1DIndex;
		sites[site1DIndex] = OPEN;
		// connect with opened neighbours
		int status = OPEN;
		if (isValidIndex(j - 1) && isOpen(i, j - 1))
		{
			status |= sites[unionFind.find(xyTo1D(i, j - 1))];
			unionFind.union(site1DIndex, xyTo1D(i, j - 1));
		}
		if (isValidIndex(j + 1) && isOpen(i, j + 1))
		{
			status |= sites[unionFind.find(xyTo1D(i, j + 1))];
			unionFind.union(site1DIndex, xyTo1D(i, j + 1));
		}
		if (isValidIndex(i - 1) && isOpen(i - 1, j))
		{
			status |= sites[unionFind.find(xyTo1D(i - 1, j))];
			unionFind.union(site1DIndex, xyTo1D(i - 1, j));
		}
		if (isValidIndex(i + 1) && isOpen(i + 1, j))
		{
			status |= sites[unionFind.find(xyTo1D(i + 1, j))];
			unionFind.union(site1DIndex, xyTo1D(i + 1, j));
		}

		if (isFirstRowSite(i, j))
		{
			// System.out.println(i + "," + j + " is first row..");
			status |= CONNECTED_TOP;
		}
		if (isLastRowSite(i, j))
		{
			// System.out.println(i + "," + j + " is bottom row..");
			status |= CONNECTED_BOTTOM;
		}

		int parent = unionFind.find(site1DIndex);
		sites[parent] = status;
		// System.out.println("parent of " + site1DIndex + " is " + parent
		// + " with status " + status);

		if ((sites[parent] & CONNECTED_TOP) == CONNECTED_TOP
				&& (sites[parent] & CONNECTED_BOTTOM) == CONNECTED_BOTTOM)
			percolates = true;
	}

	private boolean isFirstRowSite(int i, int j)
	{
		return xyTo1D(i, j) > 0 && xyTo1D(i, j) <= n;
	}

	private boolean isLastRowSite(int i, int j)
	{
		return xyTo1D(i, j) > n * (n - 1) && xyTo1D(i, j) <= n * n;
	}

	private int xyTo1D(int i, int j)
	{
		return (i - 1) * n + j;
	}

	private boolean isValidIndex(int i)
	{
		return i > 0 && i <= n;

	}

	private void validateAndFail(int i, int j)
	{
		if (!isValidIndex(i)) throw new IndexOutOfBoundsException(
				"index " + i + " is not between 1 and " + n);
		if (!isValidIndex(j)) throw new IndexOutOfBoundsException(
				"index " + i + " is not between 1 and " + n);
	}

	// is site (row i, column j) open?
	public boolean isOpen(int i, int j)
	{
		validateAndFail(i, j);
		return sites[xyTo1D(i, j)] > 0;
	}

	// is site (row i, column j) full?
	public boolean isFull(int i, int j)
	{
		validateAndFail(i, j);
		//unionFind.find(xyTo1D(i, j))
		System.out.println("xyTo1D(i, j): "+ xyTo1D(i, j));
		System.out.println("unionfind: " + unionFind.find(xyTo1D(i, j)));
		return ((sites[unionFind.find(xyTo1D(i, j))]
				& CONNECTED_TOP) == CONNECTED_TOP);
		// return isOpen(i, j) && unionFind.connected(0, xyTo1D(i, j));
	}

	// does the system percolate?
	public boolean percolates()
	{
		// return unionFind.connected(0, n * n + 1);
		return percolates;
	}

	// test client (optional)
	public static void main(String[] args)
	{
		Percolation percolation = new Percolation(20);
		System.out.println("isFull 1,1? " + percolation.isFull(1, 1));
		System.out.println("-1 binary: " + Integer.toBinaryString(-1));
//		percolation.open(1, 2);
//		System.out.println("isFull  1,2?" + percolation.isFull(1, 2));
//		
//		percolation.open(4, 4);
//		System.out.println("isFull 4,4? " + percolation.isFull(4, 4));
//		
//		percolation.open(3, 2);
//		System.out.println("isFull 3, 2? " + percolation.isFull(3, 2));
//		System.out.println(percolation.isOpen(2, 2));
//		
//		percolation.open(2, 2);
//		System.out.println("isFull 2, 2? " + percolation.isFull(2, 2));
//		System.out.println(percolation.isOpen(2, 2));
//		System.out.println("percolates: " + percolation.percolates());
//
//		percolation.open(4, 2);
//		System.out.println("isFull 4, 2? " + percolation.isFull(4, 2));
//		System.out.println("perolates: " + percolation.percolates());
//
//		percolation.open(3, 3);
//		System.out.println("isFull 3,3? " + percolation.isFull(3, 3));
//		System.out.println("perolates: " + percolation.percolates());
//
//		percolation.open(4, 4);
//		System.out.println("isFull 4, 4? " + percolation.isFull(4, 4));
//		System.out.println("perolates: " + percolation.percolates());
//
//		percolation.open(3, 4);
//		System.out.println("isFull 3, 4? " + percolation.isFull(3, 4));
//		System.out.println("perolates: " + percolation.percolates());
//
//		percolation.open(5, 4);
//		System.out.println("isFull 5, 4? " + percolation.isFull(5, 4));
//		System.out.println("perolates: " + percolation.percolates());
//
//		percolation.print(percolation.sites);
	}

}
