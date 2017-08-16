package gzs.game.misc;

public class MouseInfo {
	private Pair<Double> lmp;
	private boolean md;
	
	public Pair<Double> getPosition() { return lmp; }
	public void setPosition(double x, double y) {
		this.lmp.x = x;
		this.lmp.y = y;
	}
	
	public boolean isMouseDown() { return md; }
	public void setMouseDown(boolean md_) { this.md = md_; }
	
	public MouseInfo() {
		lmp = new Pair<Double>(0.0, 0.0);
		md = false;
	}
}