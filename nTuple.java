/*
 * nTuple.java
 * Created by: William Tyas
 * Date: 8/9/17
 * Description: A vector, with various methods to implement vector
 * operations.
 */
public class nTuple {
	private float x;
	private float y;
	private float z;

	public float getX() { return this.x; }

	public float getY() { return this.y; }

	public float getZ() { return this.z; }

	public nTuple() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}

	public nTuple(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z; 
	}

	public nTuple(nTuple other) {
		this.x = other.getX();
		this.y = other.getY();
		this.z = other.getZ();
	}

	public void setNTuple(nTuple other) {
		this.x = other.getX();
		this.y = other.getY();
		this.z = other.getZ();
	} 

	@Override
	public String toString() {
		return(this.x + "," + this.y + "," + this.z);
	}

	//////////////////////////////////////////////////////////////////
	// BASIC VECTOR OPERATIONS										//
	// Dot product, scalar multiplication, addition, subtraction,	//
	// normalize 													//
	//////////////////////////////////////////////////////////////////
	public float dot(nTuple other) {
		float newX = this.getX() * other.getX();
		float newY = this.getY() * other.getY();
		float newZ = this.getZ() * other.getZ();
		return (newX + newY + newZ);
	}

	public nTuple scale(float factor) {
		float newX = this.x * factor;
		float newY = this.y * factor;
		float newZ = this.z * factor;
		return new nTuple(newX, newY, newZ);
	}

	// Compute the product of two matrices: a 3x3 and a 3x1
	public nTuple product(nTuple a, nTuple b, nTuple c) {
		float x = a.dot(this);
		float y = b.dot(this);
		float z = c.dot(this);
		return new nTuple(x, y, z);
	}

	public nTuple add(nTuple other) {
		float newX = this.x + other.getX();
		float newY = this.y + other.getY();
		float newZ = this.z + other.getZ();
		return new nTuple(newX, newY, newZ);
	}

	public nTuple subtract(nTuple other) {
		float newX = this.x - other.getX();
		float newY = this.y - other.getY();
		float newZ = this.z - other.getZ();
		return new nTuple(newX, newY, newZ);
	} 

	public nTuple normalize() {
		float len = (float) Math.sqrt(this.dot(this));
		return new nTuple(this.x / len, this.y / len, this.z / len); 
	}

	//////////////////////////////////////////////////////////////////
	// GAUSS-JORDAN ELIMINATION										//
	//////////////////////////////////////////////////////////////////

	// Calculate the reduced row echelon form of a matrix
	public static void rref(float[][] matrix) {
		// Forward phase - results in reduced echelon form
		for (int i = 0; i < matrix.length; i++) {
			// Find next leftmost nonzero column from rows below i
			int colIndex = nonZeroCol(matrix, i, matrix.length - 1);
			if (colIndex >= 0.0f) {
				// Bring nonzero entry to top of column
				if (matrix[i][colIndex] == 0.0f) {
					int j = nextNonZeroRow(matrix, i, colIndex);
					rowSwap(matrix, i, j);
				}
				float pivot = matrix[i][colIndex];

				// Multiply row to make pivot a 1
				if (pivot != 1.0f && !closeEnough(pivot, 0.0f)) {
					pivot = 1.0f / matrix[i][colIndex];
					for (int k = 0; k < matrix[0].length; k++) {
						float newValue = matrix[i][k] * pivot;
						if (closeEnough(newValue, Math.round(newValue))) {
							matrix[i][k] = Math.round(newValue);
						} else {
							matrix[i][k] = newValue;
						}
					}
				}

				// Add multiples of row to others below
				for (int k = (i + 1); k < matrix.length; k++) {
					float value = matrix[k][colIndex];
					if (value != 0.0f) {
						float first = matrix[k][colIndex];
						for (int el = 0; el < (matrix[0].length); el++) {
							float newValue = -first * matrix[i][el] + matrix[k][el];
							if (closeEnough(newValue, Math.round(newValue))) {
								matrix[k][el] = Math.round(newValue);
							} else {
								matrix[k][el] = newValue;
							}
						}
					}
				}
			}
		}

		// Backward phase - Add multiples of row to others above
		// Results in rref
		int lastNonZeroRow = lastNonZeroRow(matrix);
		for (int k = lastNonZeroRow; k > 0; k--) {
			int firstNonZeroCol = nonZeroCol(matrix, k, k); 
			for (int i = 0; i < k; i++) {
				float value = matrix[i][firstNonZeroCol];
				if (value != 0.0f) {
					float first = matrix[i][firstNonZeroCol];
					for (int el = 0; el < (matrix[0].length); el++) {
						float newValue = -first * matrix[k][el] + matrix[i][el];
						if (closeEnough(newValue, Math.round(newValue))) {
							matrix[i][el] = Math.round(newValue);
						} else {
							matrix[i][el] = newValue;
						}
					}
				}
			}
		}
	}

	public static boolean closeEnough(float pivot, float limit) {
		return (Math.abs(limit - pivot) < 0.001f);
	}

	// Finds the next nonzero column in a matrix between minRow and maxRow
	public static int nonZeroCol(float[][] matrix, int minRow, int maxRow) {
		for (int i = 0; i < matrix[0].length; i++) {
			for (int j = minRow; j <= maxRow; j++) {
				if (matrix[j][i] != 0.0f) {
					return i;
				}
			}
		}
		return -1;
	}

	// Finds next row below rowIndex with nonzero entry in column colIndex
	public static int nextNonZeroRow(float[][] matrix, int rowIndex, int colIndex) {
		for (int i = (rowIndex + 1); i < matrix.length; i++) {
			if (matrix[i][colIndex] != 0.0f) {
				return i;
			}
		}
		return -1;
	}

	// Finds the last nonzero row in a matrix
	public static int lastNonZeroRow(float[][] matrix) {
		boolean zeroRow = true;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (matrix[i][j] != 0.0f) {
					zeroRow = false;
				}
			}
			if (zeroRow == true) {
				return (i - 1);
			} else if (i == (matrix.length - 1)) { // last row
				return i;
			}
			zeroRow = true;
		}
		return -1;
	}

	public static void rowSwap(float[][] matrix, int first, int second) {
		for (int i = 0; i < matrix[0].length; i++) {
			float tmp = matrix[first][i];
			matrix[first][i] = matrix[second][i];
			matrix[second][i] = tmp;
		}
	}

	// Find coordinates of d in coordinate system defined by basis
	// vectors a, b, and c
	public nTuple coordChange(nTuple a, nTuple b, nTuple c, nTuple d) {
		float[][] basisChange = new float[3][4];
		basisChange[0][0] = a.getX();
		basisChange[0][1] = b.getX();
		basisChange[0][2] = c.getX();
		basisChange[0][3] = d.getX();
		basisChange[1][0] = a.getY();
		basisChange[1][1] = b.getY();
		basisChange[1][2] = c.getY();
		basisChange[1][3] = d.getY();
		basisChange[2][0] = a.getZ();
		basisChange[2][1] = b.getZ();
		basisChange[2][2] = c.getZ();
		basisChange[2][3] = d.getZ();
		this.rref(basisChange);
		return new nTuple(basisChange[0][3], basisChange[1][3], basisChange[2][3]);
	}
}
