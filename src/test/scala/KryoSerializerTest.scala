import java.util.UUID

import edu.bupt.yac.commons.YacConf
import edu.bupt.yac.serializer.KryoSerializer

import scala.util.Random

/**
 * Created by chenlingpeng on 15/9/22.
 */
object KryoSerializerTest extends App {
  val items = List.fill(3000)(Create.randomWeaponAccessory())
  items.filter(_.guid.startsWith("a")).foreach(w => println(w.guid))
  val conf = new YacConf
  val serializer = new KryoSerializer(conf)
  val kryo = serializer.newInstance()
  val res = kryo.serialize(items)
  println(res.array().toString)
  val ds = serializer.newInstance().deserialize[List[WeaponAccessory]](res)
  ds.filter(_.guid.startsWith("a")).foreach(w=>println(w.guid))

  val item = Create.randomWeaponAccessory()
  println(item.guid)
  println(serializer.newInstance().deserialize[WeaponAccessory](serializer.newInstance().serialize(item)).guid)

  val item2 = Create.randomWeaponAccessory()
  println(item2.guid)
  println(kryo.deserialize[WeaponAccessory](kryo.serialize(item2)).guid)
}

class WeaponAccessory(
                       var guid: String,
                       var slug: Option[String],
                       var nameSID: Option[String],
                       var descriptionSID: Option[String],
                       var categorySID: Option[String],
                       var requirements: Option[List[UnlockRequirement]],
                       var weaponData: Option[WeaponData]) {
  def this() = this("", None, None, None, None, None, None)
}

class UnlockRequirement(
                         var requirementType: RequirementType,
                         var valueNeeded: Float,
                         var codeNeeded: String) {
  def this() = this(Bucket, 0.0F, "")
}

class WeaponData(
                  var statDamage: Option[Float],
                  var statAccuracy: Option[Float],
                  var statMobility: Option[Float],
                  var statRange: Option[Float],
                  var statHandling: Option[Float]) {
  def this() = this(None, None, None, None, None)
}

sealed trait RequirementType {
  def name() = getClass.getSimpleName
}

object Bucket extends RequirementType

object Create {
  def randomWeaponAccessory() = {
    val accessory = new WeaponAccessory()
    accessory.guid = "%s".format(UUID.randomUUID())
    accessory.slug = randomOption(randString(10))
    accessory.nameSID = randomOption(randString(25))
    accessory.descriptionSID = randomOption(randString(25))
    accessory.categorySID = randomOption(randString(25))
    accessory.requirements = Some(
      List.fill(Random.nextInt(5))(randomRequirement())
    )
    accessory.weaponData = randomOption(randomWeaponData())
    accessory
  }

  private def randomRequirement() = {
    val requirement = new UnlockRequirement()
    requirement.requirementType = Bucket
    requirement.codeNeeded = randString(5)
    requirement.valueNeeded = Random.nextFloat()
    requirement
  }

  private def randomWeaponData() = {
    val weaponData = new WeaponData()
    weaponData.statAccuracy = randomOption(Random.nextFloat())
    weaponData.statDamage = randomOption(Random.nextFloat())
    weaponData.statHandling = randomOption(Random.nextFloat())
    weaponData.statMobility = randomOption(Random.nextFloat())
    weaponData.statRange = randomOption(Random.nextFloat())
    weaponData
  }

  private def randomOption[T](value: T) = {
    if (Random.nextBoolean()) {
      Some(value)
    } else {
      None
    }
  }

  private def randString(x: Int) = Random.alphanumeric.take(x).mkString
}
