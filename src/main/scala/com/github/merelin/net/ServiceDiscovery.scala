package com.github.merelin.net

import java.net.InetAddress
import java.util.{Timer, TimerTask}
import javax.jmdns._

case class Host(ip: String, name: String)

object Host {
  def apply(address: InetAddress): Host = new Host(ip = address.getHostAddress, name = address.getHostName)
}

case class DNSListener(fnAdded: (ServiceEvent) => Unit, fnRemoved: (ServiceEvent) => Unit) extends ServiceListener {
  override def serviceAdded(event: ServiceEvent): Unit = fnAdded(event)
  override def serviceResolved(event: ServiceEvent): Unit = fnAdded(event)
  override def serviceRemoved(event: ServiceEvent): Unit = fnRemoved(event)
}

case class DNSTypeListener(fn: (ServiceEvent) => Unit) extends ServiceTypeListener {
  override def serviceTypeAdded(event: ServiceEvent): Unit = fn(event)
  override def subTypeForServiceTypeAdded(event: ServiceEvent): Unit = fn(event)
}

class ServiceDiscovery {
  import ServiceDiscovery._

  // It may be worth using DiscoveryCallback
  private var hosts = Set.empty[Host]

  def discoveredHosts: Set[Host] = hosts.synchronized { hosts }

  preferIPv4Stack()

  private val jmdns = JmDNS.create

  val listener = DNSListener(
    fnAdded = (e: ServiceEvent) => withAddresses(e) { a => hosts.synchronized { hosts += Host(a) } },
    fnRemoved = (e: ServiceEvent) => withAddresses(e) { a => hosts.synchronized { hosts -= Host(a) } }
  )

  val typeListener = DNSTypeListener(
    fn = (e: ServiceEvent) => if (types.contains(e.getType)) jmdns.addServiceListener(e.getType, listener)
  )

  jmdns.addServiceTypeListener(typeListener)
}

object ServiceDiscovery {
  // Known Popcorn Hour A-400 types
  val types = List("_syb._tcp.local.", "_airplay._tcp.local.", "_raop._tcp.local.")
  val defaultTimeout = 6000

  def preferIPv4Stack(): Unit = System.setProperty("java.net.preferIPv4Stack", "true")

  def withAddresses(e: ServiceEvent)(fn: (InetAddress) => Unit): Unit =
    e.getDNS.getServiceInfo(e.getType, e.getName).getInetAddresses.foreach(fn)

  def main(args: Array[String]): Unit = {
    val sd = new ServiceDiscovery
    new Timer().scheduleAtFixedRate(
      new TimerTask() {
        override def run(): Unit = {
          println(s"hosts: ${sd.discoveredHosts}")
        }
      },
      0,
      1000
    )
  }
}
