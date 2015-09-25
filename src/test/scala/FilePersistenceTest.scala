import edu.bupt.yac.commons.YacConf
import edu.bupt.yac.deploy.master.FileSystemPersistenceEngine
import edu.bupt.yac.serializer.KryoSerializer

/**
 * Created by chenlingpeng on 15/9/23.
 */
object FilePersistenceTest extends App{
  val lists = List.fill(1000)(Create.randomWeaponAccessory())
  lists.foreach(a=>println(a.guid))
  val conf = new YacConf
  val engine = new FileSystemPersistenceEngine(".", new KryoSerializer(conf))
  engine.persist("haha", lists)
  val data = engine.read[List[WeaponAccessory]]("haha")
  data.get.foreach(a=>println(a))
}
