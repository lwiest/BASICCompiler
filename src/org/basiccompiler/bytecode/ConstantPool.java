package org.basiccompiler.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.basiccompiler.bytecode.constantpoolinfo.ConstantPoolInfo;

public class ConstantPool {
	private List<ConstantPoolInfo> listOfConstantPoolInfos = new ArrayList<ConstantPoolInfo>();
	private Map<String, Integer> mapOfKeys = new HashMap<String, Integer>();

	public void put(String key, ConstantPoolInfo constantPoolInfo) {
		int nextIndex = this.listOfConstantPoolInfos.size();

		this.listOfConstantPoolInfos.add(constantPoolInfo);
		this.mapOfKeys.put(key, nextIndex);
	}

	public ConstantPoolInfo get(int constantPoolIndex) {
		return this.listOfConstantPoolInfos.get(constantPoolIndex);
	}

	public int getIndex(String key) {
		Integer anIndex = this.mapOfKeys.get(key);
		if (anIndex != null) {
			return anIndex.intValue();
		}
		return -1;
	}

	public boolean contains(String key) {
		return this.mapOfKeys.containsKey(key);
	}

	public int size() {
		return this.listOfConstantPoolInfos.size();
	}

	public ConstantPoolInfo[] getConstantPoolInfos() {
		return this.listOfConstantPoolInfos.toArray(new ConstantPoolInfo[0]);
	}
}
