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
package sophia.mmorpg.utils;

import sophia.foundation.core.ComponentRegistry;
import sophia.foundation.core.ComponentRegistryImpl;

public final class RuntimeResult {
	public static final int OKResult = 1;

	private static final int ParameterErrorResult = 2;
	private static final String UnknownParamterErrorInfo = "未能确定的参数错误";

	private static final int RuntimeErrorResult = 3;
	private static final String UnknownRuntimeErrorInfo = "未能确定的运行时错误";

	private static final int RuntimeApplicationErrorResult = 4;
	private static final String RuntimeApplicationErrorInfo = "逻辑应用运行时错误";

	private int code = OKResult;
	private int applicationCode = 0;

	private String details = null;

	private ComponentRegistry data = new ComponentRegistryImpl();

	private RuntimeResult() {

	}

	private RuntimeResult(int code, String details) {
		this.code = code;
		this.details = details;
	}

	private RuntimeResult(int code, int applicationCode, String details) {
		this.code = code;
		this.setApplicationCode(applicationCode);
		this.details = details;
	}

	public boolean isOK() {
		return this.code == OKResult;
	}

	public boolean isError() {
		return this.code == ParameterErrorResult || this.code == RuntimeErrorResult || this.code == RuntimeApplicationErrorResult;
	}

	public boolean isApplicationError() {
		return this.code == RuntimeApplicationErrorResult;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public static final RuntimeResult OK() {
		return new RuntimeResult(OKResult, null);
	}

	public static final RuntimeResult ParameterError() {
		return new RuntimeResult(ParameterErrorResult, UnknownParamterErrorInfo);
	}

	public static final RuntimeResult ParameterError(String details) {
		return new RuntimeResult(ParameterErrorResult, details.intern());
	}

	public static final RuntimeResult RuntimeError() {
		return new RuntimeResult(RuntimeErrorResult, UnknownRuntimeErrorInfo);
	}

	public static final RuntimeResult RuntimeError(String details) {
		return new RuntimeResult(RuntimeErrorResult, details.intern());
	}

	public static final RuntimeResult RuntimeApplicationError(int code) {
		return new RuntimeResult(RuntimeApplicationErrorResult, code, RuntimeApplicationErrorInfo.intern());
	}

	public ComponentRegistry getData() {
		return data;
	}

	public void setData(ComponentRegistry data) {
		this.data = data;
	}

	public int getApplicationCode() {
		return applicationCode;
	}

	public void setApplicationCode(int applicationCode) {
		this.applicationCode = applicationCode;
	}

	@Override
	public String toString() {
		return "RuntimeResult [code=" + code + ", details=" + details + ", data=" + data + "]";
	}

}
