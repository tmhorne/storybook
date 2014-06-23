package ch.intertec.storybook.model;

@SuppressWarnings("serial")
public class EmptyScene extends Scene {

	public EmptyScene(){
		super(true);
	}
	
	@Override
	public String toString() {
		return "";
	}
}
