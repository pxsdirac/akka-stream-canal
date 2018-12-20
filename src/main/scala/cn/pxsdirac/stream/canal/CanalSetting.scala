package cn.pxsdirac.stream.canal

/**
 * setting to connect Canal server
 *
 * @param servers server list,list of (Host,Port)
 * @param destination
 * @param username
 * @param password
 * @param enableAutoAck
 */
case class CanalSetting(servers: List[(String, Int)],
                        destination: String,
                        username: String = "",
                        password: String = "",
                        enableAutoAck: Boolean = false,
                        bufferSize: Int = 1,
                        subscribeFilter: String = "") {
  require(servers.size > 0, "the servers should more than one")
  require(bufferSize > 0, "the bufferSize should larger than one")
}
