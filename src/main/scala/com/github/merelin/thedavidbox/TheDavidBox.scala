package com.github.merelin.thedavidbox

case class TheDavidBox(request: Request, response: Response, returnValue: Int)

case class Request(args: List[String], module: String)

case class Response(items: List[AnyRef])

case class Language(language: String)

case class PlayMode(playMode: String)

case class Style(style: String)

case class Time(time: Long)

case class SetupPageLock(setupPageLock: Boolean)

case class VideoOutput(videoOutput: String)

case class FrameRate(frameRate: String)

case class VideoZoom(videoZoom: String)

case class TvType(tvType: String)

case class ColorSpace(colorSpace: String)

case class HdmiAudio(hdmiAudio: Boolean)

case class DtsAudioMode(dtsAudioMode: String)

case class Ac3AudioMode(ac3AudioMode: String)

case class AacAudioMode(aacAudioMode: String)

case class WmaProAudioMode(wmaProAudioMode: String)

case class PcmAudioMode(pcmAudioMode: String)

case class NetworkMode(networkMode: String)

case class LinkUp(linkUp: Boolean)

case class TimeZone(timeZone: String)

case class TimeServer(timeServer: String)

case class DaylightSaving(daylightSaving: Boolean)

case class Gateway(gateway: String)

case class IpAddress(ipAddress: String)

case class PrimaryDns(primaryDns: String)

case class SecondaryDns(secondaryDns: String)

case class SubnetMask(subnetMask: String)

case class NetworkShare(shareName: String, url: String)

case class Level(level: String)

case class AutoPlay(autoPlay: Boolean)

