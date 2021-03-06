/**
 * 
 */
package com.brimud.command.builder;

import com.brimud.command.Command;
import com.brimud.db.RoomDao;
import com.brimud.db.ZoneDao;
import com.brimud.model.Player;
import com.brimud.model.Room;
import com.brimud.model.RoomId;
import com.brimud.model.Zone;
import com.brimud.service.MessageService;
import com.google.inject.Inject;

/**
 * @author dan
 *
 */
class ZoneStartingRoomCommand implements Command {
  
  static final String ZONE_STARTING_ROOM = "zstartroom";
  
  private final MessageService messageService;
  
  private final RoomDao roomDao;
  
  private final ZoneDao zoneDao;
  
  @Inject
  ZoneStartingRoomCommand(MessageService messageService, RoomDao roomDao, ZoneDao zoneDao) {
    this.messageService = messageService;
    this.roomDao = roomDao;
    this.zoneDao = zoneDao;
  }
  
  /* (non-Javadoc)
   * @see com.brimud.command.Command#doCommand(com.brimud.model.Player, java.lang.String, java.lang.String)
   */
  @Override
  public void doCommand(Player player, String command, String arguments) {
    if (arguments == null) {
      messageService.sendMessage(player, "What zone.room?");
      return;
    }
    
    RoomId startingRoomId = RoomId.fromString(arguments);
    if (startingRoomId == null) {
      messageService.sendMessage(player, "Specify a zone and room (" + ZONE_STARTING_ROOM + " zone1.room1)");
      return;
    }
    
    Room startingRoom = roomDao.getById(startingRoomId);
    if (startingRoom == null) {
      messageService.sendMessage(player, "Room " + startingRoomId + " doesn't exist!");
      return;
    }
    
    Zone zoneToUpdate = zoneDao.getById(startingRoom.getId().getZone().getId());
    zoneToUpdate.setStartingRoom(startingRoom.getId().getRoomId());
    zoneDao.saveOrUpdate(zoneToUpdate);
    
    messageService.sendMessage(player, "Starting room set to " + startingRoomId);
  }

}
