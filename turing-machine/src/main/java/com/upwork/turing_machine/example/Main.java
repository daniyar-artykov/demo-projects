package com.upwork.turing_machine.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main  {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args)	{
		TuringMachine tm = MachineLibrary.equalBinaryWords();

		boolean done = tm.run("010000110101#010000110101", false);
		if (done == true) {
			LOG.info("The input was accepted.");
		} else {
			LOG.info("The input was rejected.");
		}
	}
}
