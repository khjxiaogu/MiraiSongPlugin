package com.khjxiaogu.MiraiSongPlugin.permission;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;

public class MatchInfo {
	Bot bot;
	long callerid;
	long groupid;
	String cmd;
	boolean isTemp;
	boolean mustMatchCommand;
	public MatchInfo() {
	}
	public MatchInfo(String cmd,long memberid,long groupid,Bot bot) {
		super();
		this.bot = bot;
		this.callerid = memberid;
		this.groupid = groupid;
		this.cmd = cmd;
	}
	public MatchInfo(String cmd,Member m) {
		this(cmd,m.getId(),m.getGroup().getId(),m.getBot());
	}

	public MatchInfo(String cmd,User u,boolean temp) {
		this(cmd,u.getId(),0,u.getBot());
		this.isTemp=temp;
	}
	public MatchInfo mustMatchCommand() {
		mustMatchCommand=true;
		return this;
	}
}
