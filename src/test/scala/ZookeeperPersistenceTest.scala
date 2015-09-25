import edu.bupt.yac.commons.YacConf
import edu.bupt.yac.deploy.master.ZookeeperPersistenceEngine
import edu.bupt.yac.serializer.KryoSerializer

/**
 * Created by chenlingpeng on 15/9/24.
 */
object ZookeeperPersistenceTest extends App{
  val lists = List.fill(10)(Create.randomWeaponAccessory())
  lists.foreach(a=>println(a.guid))
  val conf = new YacConf
  val engine = new ZookeeperPersistenceEngine(conf, new KryoSerializer(conf))
  engine.persist("haha", lists)
  val data = engine.read[List[WeaponAccessory]]("haha")
  data.get.foreach(a=>println(a.guid))
}
