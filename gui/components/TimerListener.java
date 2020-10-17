package gui.components;

public interface TimerListener {
	
	public void timerFinished();
	public void timerStarted();
	
	
	public static class TimerAdapter implements TimerListener{

		@Override
		public void timerFinished() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void timerStarted() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
