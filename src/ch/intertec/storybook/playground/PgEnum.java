package ch.intertec.storybook.playground;

public class PgEnum {
	public enum PgContentPanelType {
		BLANK, CHRONO, BOOK, MANAGE;
		int scale;
	}
	
	public enum Dummy {
		VALUE1("value 1"), VALUE2("value 2");
		private String text;
		private Dummy(String text) {
			this.text = text;
		}
		public String getText() {
			return text;
		}
	}
	
	public static void main(String[] args) {
		System.out.println("start");
		PgEnum pge = new PgEnum();
		pge.doit2();
		pge.doit1();
		System.out.println("end");
	}
	
	private void doit2() {
		Dummy d = Dummy.VALUE1;
		System.out.println(d);
		System.out.println("text: " + d.getText());
	}
	
	private void doit1() {
		PgContentPanelType cp = PgContentPanelType.BLANK;
		System.out.println("blank: " + cp);
		cp.scale = 2;
		System.out.println("blank scale: " + cp.scale);
		cp = PgContentPanelType.CHRONO;
		System.out.println("chrono scale: " + cp.scale);
		cp.scale = 34;
		System.out.println("chrono scale: " + cp.scale);
		cp = PgContentPanelType.BLANK;
		System.out.println("blank scale: " + cp.scale);
		cp = PgContentPanelType.CHRONO;
		System.out.println("chrono scale: " + cp.scale);
	}
}
