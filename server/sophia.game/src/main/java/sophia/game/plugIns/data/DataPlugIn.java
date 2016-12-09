/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
*/
package sophia.game.plugIns.data;

import org.apache.log4j.Logger;

import sophia.foundation.core.FoundationContext;
import sophia.foundation.data.DataService;
import sophia.foundation.data.DataServiceImpl;
import sophia.foundation.util.PropertiesWrapper;
import sophia.game.core.PlugIn;

import com.google.common.util.concurrent.Service.State;

public class DataPlugIn implements PlugIn<DataService> {
	
	private static final Logger logger = Logger.getLogger(DataPlugIn.class);
	
	private DataService dataService;
	
	@Override
	public DataService getModule() {
		return dataService;
	}

	@Override
	public void initialize() {
		dataService = new DataServiceImpl();
		PropertiesWrapper properties = FoundationContext.getProperties();
		dataService.setDataSaveIntervalTime(properties.getLongProperty(
				DataService.Interval_Time_Property,
				DataService.Default_Interval_Time));
		dataService.setEnabled(properties.getBooleanProperty(
				DataService.Enable_Property, DataService.Default_Enable));
	}

	@Override
	public void start() {
		State state = dataService.startAndWait();
		if (state == State.RUNNING) {
			logger.info("DataService was running.");
		} else {
			logger.error("DataService start failed. the server will run without DataService.");
			throw new RuntimeException("DataService start failed. the server will run without DataService.");
		}
	}

	@Override
	public void stop() {
		State state = dataService.stopAndWait();
		if (state == State.TERMINATED) {
			logger.info("DataService was terminated.");
		} else {
			logger.error("DataService stop failed.");
		}
	}

	@Override
	public void cleanUp() {
		
	}

}
