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
package newbee.morningGlory.debug;

import java.util.Arrays;

import sophia.mmorpg.player.Player;

public abstract class DebugCommand {

	private String commandTemplate;
	private String description;
	private String[] examples;

	public DebugCommand(String commandTemplate, String description,
			String... examples) {
		this.commandTemplate = commandTemplate;
		this.description = description;
		this.examples = examples;
	}

	public String getCommandTemplate() {
		return commandTemplate;
	}

	public void setCommandTemplate(String commandTemplate) {
		this.commandTemplate = commandTemplate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getExamples() {
		return examples;
	}

	public void setExamples(String[] examples) {
		this.examples = examples;
	}

	public abstract String exec(Player player, CommandParameters parameters);

	public static class CommandParameters {
		private String[] args;

		public CommandParameters(String commandStr) {
			// 中文空格
			commandStr = commandStr.replaceAll("　", " ");
			// 多个空格
			commandStr = commandStr.replaceAll("  ", " ");
			commandStr = commandStr.replaceAll("  ", " ");
			args = commandStr.split(" ");
		}

		public String getString(int index) {
			return args[index];
		}

		public int getInt(int index) {
			return Integer.parseInt(args[index]);
		}

		public short getShort(int index) {
			return Short.parseShort(args[index]);
		}

		public byte getByte(int index) {
			return Byte.parseByte(args[index]);
		}

		public boolean getBoolean(int index) {
			return getInt(index) > 0;
		}

		@Override
		public String toString() {
			return "CommandParameters [args=" + Arrays.toString(args) + "]";
		}
	}

	@Override
	public String toString() {
		return "DebugCommand [commandTemplate=" + commandTemplate
				+ ", description=" + description + ", examples="
				+ Arrays.toString(examples) + "]";
	}
}
