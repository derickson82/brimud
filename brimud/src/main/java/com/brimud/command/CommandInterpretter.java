/**
 * 
 */
package com.brimud.command;

import java.util.Map;

import com.brimud.command.builder.BuilderCommands;
import com.brimud.db.PlayerDao;
import com.brimud.filter.ChainTarget;
import com.brimud.model.Player;
import com.brimud.service.MessageService;
import com.brimud.session.Session;
import com.brimud.util.StringUtil;
import com.google.inject.Inject;

/**
 * @author dan
 * 
 */
public class CommandInterpretter implements ChainTarget {

  private final PlayerDao playerDao;

  private final MessageService messageService;

  private final Map<String, Command> commands;
  
  private final Map<String, Command> builderCommands;

  @Inject
  CommandInterpretter(PlayerDao playerDao, MessageService messageService,
      @GeneralCommands Map<String, Command> commands, @BuilderCommands Map<String, Command> builderCommands) {
    this.playerDao = playerDao;
    this.messageService = messageService;
    this.commands = commands;
    this.builderCommands = builderCommands;
  }

  public void interpret(final Player player, final String command) {

    String trimmedCommand = StringUtil.trimToNull(command);
    if (trimmedCommand == null) {
      return;
    }

    final String comm;
    String arguments = null;
    int index;
    if ((index = trimmedCommand.indexOf(' ')) > 0) {
      comm = trimmedCommand.substring(0, index);
      arguments = trimmedCommand.substring(index + 1);
    } else {
      comm = trimmedCommand;
    }
    Command toExecute = commands.get(comm);
    if (toExecute == null) {
      toExecute = builderCommands.get(comm);
    }
    if (toExecute != null) {
      toExecute.doCommand(player, comm, StringUtil.trimToNull(arguments));
    } else {
      messageService.sendMessage(player, "Huh?!");
    }
  }

  @Override
  public void execute(Session session, String command) {
    Player player = playerDao.getById(session.getAccount().getPlayer().getName());
    interpret(player, command);
  }

}
