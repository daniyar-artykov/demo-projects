package com.upwork.turing_machine.example; 

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TuringMachine {
	
	private static final Logger LOG = LoggerFactory.getLogger(TuringMachine.class);
	
	private Set<String> stateSpace;
	private Set<Transition> transitionSpace;
	private String startState;
	private String acceptState;
	private String rejectState;
	
	private String tape;
	private String currentState;
	private int currentSymbol;
		
	class Transition {
		
		String readState;
		char readSymbol;
		String writeState;
		char writeSymbol;
//		int totalGreater
		boolean moveDirection;	//true is right, false is left
		
		boolean isConflicting(String state, char symbol) {
			if (state.equals(readState) && symbol == readSymbol) {
				return true;
			} else {
				return false;			
			}
		}		
	}
	
	
	public TuringMachine() {
		
		stateSpace = new HashSet<String>();
		transitionSpace = new HashSet<Transition>();
		startState = new String("");
		acceptState = new String("");
		rejectState = new String("");
		tape = new String("");
		currentState = new String("");
		currentSymbol = 0;
		
	}
	
	public boolean run(String input, boolean silentmode) {
		currentState = startState;
		tape = input;
		
		while(!currentState.equals(acceptState) && !currentState.equals(rejectState)) {
			boolean foundTransition = false;
			Transition currentTransition = null;
			
			if (!silentmode) {
				if (currentSymbol > 0) {
					LOG.info("{} {} {}", tape.substring(0, currentSymbol), 
							currentState, tape.substring(currentSymbol));
				} else {
					LOG.info(" {} {}", currentState, tape.substring(currentSymbol));
				}
			}
			
			Iterator<Transition> transitionsIterator = transitionSpace.iterator();
			while ( transitionsIterator.hasNext() && !foundTransition) {
				Transition nextTransition = transitionsIterator.next();
				if (nextTransition.readState.equals(currentState) 
						&& nextTransition.readSymbol == tape.charAt(currentSymbol)) {
					foundTransition = true;
					currentTransition = nextTransition;
				}
		    }	
			
			if (!foundTransition) {
				LOG.error("There is no valid transition for this phase! (state={}, symbol={})", 
						currentState, tape.charAt(currentSymbol));
				return false;
			} else {
				currentState = currentTransition.writeState;
				char[] tempTape = tape.toCharArray(); 				
				tempTape[currentSymbol] = currentTransition.writeSymbol;
				tape =  new String(tempTape);
				if(currentTransition.moveDirection) {
					currentSymbol++;
				} else {
					currentSymbol--;
				}
				
				if (currentSymbol < 0) {
					currentSymbol = 0;
				}
				
				while (tape.length() <= currentSymbol) {
					tape = tape.concat("_");
				}
			}
		}
		
		if (currentState.equals(acceptState)) {
			return true;
		} else {
			return false;
		}	
	}
	
	public boolean addState(String newState) {
		if (stateSpace.contains(newState)) {
			return false;
		} else {
			stateSpace.add(newState);
			return true;
		}
	}
	
	public boolean setStartState(String newStartState) {
		if (stateSpace.contains(newStartState)) {
			startState = newStartState;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean setAcceptState(String newAcceptState) {
		if (stateSpace.contains(newAcceptState) && !rejectState.equals(newAcceptState)) {
			acceptState = newAcceptState;
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean setRejectState(String newRejectState) {
		if (stateSpace.contains(newRejectState) && !acceptState.equals(newRejectState)) {
			rejectState = newRejectState;
			return true;
		} else {
			return false;
		}		
	}

	public boolean addTransition(String rState, char rSymbol, String wState, 
			char wSymbol, boolean mDirection) {
		if(!stateSpace.contains(rState) || !stateSpace.contains(wState)) {
			return false;
		}
			
		boolean conflict = false;
		Iterator<Transition> transitionsIterator = transitionSpace.iterator();
		while (transitionsIterator.hasNext() && !conflict) {
			Transition nextTransition = transitionsIterator.next();
			if (nextTransition.isConflicting(rState, rSymbol)) {
				conflict = true;
			}
	    }
		
		if (conflict) {
			return false;
		} else {
			Transition newTransition = new Transition();
			newTransition.readState = rState;
			newTransition.readSymbol = rSymbol;
			newTransition.writeState = wState;
			newTransition.writeSymbol = wSymbol;
			newTransition.moveDirection = mDirection;
			transitionSpace.add(newTransition);
			
			return true;
		}
	}
}
