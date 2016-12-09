/**
 * 
 */
package sophia.foundation.util;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 黄晓源 Create on 2012-9-26 上午10:09:36
 * @version 1.0
 */
public final class Position {
	private int x;
	private int y;
	
	public Position() {
		
	}
	
	public Position(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public final int getX() {
		return x;
	}

	public final void setX(int x) {
		this.x = x;
	}

	public final int getY() {
		return y;
	}

	public final void setY(int y) {
		this.y = y;
	}
	
	public final void setPosition(final int x, final int y) {
		this.x = x;
		this.y = y;
	}



	@Override
	public String toString() {
		return "Point2D[x=" + x + ", y=" + y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
