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
package newbee.morningGlory.ref.loader;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.InputStream;
import java.nio.ByteBuffer;

import newbee.morningGlory.ref.symbol.PropertySymbolLoader;
import newbee.morningGlory.ref.utils.PropertyHelper;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import sophia.mmorpg.base.scene.grid.SceneTerrainGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;

public class MapLoader {
	private static Logger logger = Logger.getLogger(MapLoader.class);

	@SuppressWarnings("unused")
	private static SceneTerrainLayer load(byte[] mapBlockData) {
		checkArgument(mapBlockData != null);
		ByteBuffer bf = ByteBuffer.wrap(mapBlockData);
		byte layerType = bf.get();
		short layerID = bf.getShort();
		short layerNameLen = bf.getShort();
		byte[] layerNameData = new byte[layerNameLen];
		bf.get(layerNameData);
		// String name = new String(layerNameData);

		int version = bf.getInt();
		int cellWidth = bf.getInt();
		int cellHeight = bf.getInt();
		int nWidth = bf.getInt();
		int nHeight = bf.getInt();
		short compress = bf.getShort();
		byte metaType = bf.get();

		// useless fields
		short nColorInfo = bf.getShort();
		for (int i = 0; i < nColorInfo; i++) {
			int color = bf.getInt();
			byte colorId = bf.get();
		}

		SceneTerrainGrid[][] matrix = new SceneTerrainGrid[nHeight][nWidth];
		for (int row = 0; row < nHeight; row++) {
			for (int column = 0; column < nWidth; column++) {
				byte blocked = bf.get();
				SceneTerrainGrid grid = new SceneTerrainGrid((short) row, (short) column, blocked, nWidth, nHeight);
				matrix[row][column] = grid;
			}
		}
		SceneTerrainLayer layer = new SceneTerrainLayer(matrix);
		return layer;
	}

	public static SceneTerrainLayer load(int mapId) {
		PropertyHelper helper = new PropertyHelper();
		String mapDir = helper.getProperty("mapDir");
		String mapPath = mapDir.concat("/" + mapId + ".sm");
		if (logger.isDebugEnabled()) {
			logger.debug("load map file: " + mapPath);
		}
		InputStream is = PropertySymbolLoader.class.getClassLoader().getResourceAsStream(mapPath);
		try {
			byte[] map = IOUtils.toByteArray(is);
			return load(map);
		} catch (Exception e) {
			logger.error("map can't find mapId = " + mapId);
			e.printStackTrace();
		}
		return null;
	}

}
