1. 先建立数据库，这里数据库命名为morningGlory_data, morningGlory_log, 这2个数据库分别是玩家信息数据库，玩家日志数据库，都是utf8、utf8-general-ci编码，并且导入对应的sql文件
2. 修改morningGlory.gameserver工程目录下面的src/resources/morningGlory_data.xml，src/resources/morningGlory_log.xml文件，指向正确的数据库ip,用户名密码
3. 在eclipse环境下，指向morningGlory.gameserver下面的GameApp.java，游戏服务器既可以运行起来