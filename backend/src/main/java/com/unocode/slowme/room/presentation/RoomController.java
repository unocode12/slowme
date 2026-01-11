package com.unocode.slowme.room.presentation;

import com.unocode.slowme.room.application.RoomService;
import com.unocode.slowme.room.application.dto.CreateRoomRequest;
import com.unocode.slowme.room.application.dto.JoinLeaveRoomRequest;
import com.unocode.slowme.room.application.dto.RoomSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rooms")
public class RoomController {
	private final RoomService roomService;

	@PostMapping
	public RoomSummaryResponse create(@Valid @RequestBody CreateRoomRequest req) {
		return roomService.createRoom(req);
	}

	@GetMapping
	public List<RoomSummaryResponse> list() {
		return roomService.listOpenRooms();
	}

	@PostMapping("/{roomId}/join")
	public RoomSummaryResponse join(@PathVariable Long roomId, @Valid @RequestBody JoinLeaveRoomRequest req) {
		return roomService.join(roomId, req);
	}

	@PostMapping("/{roomId}/leave")
	public RoomSummaryResponse leave(@PathVariable Long roomId, @Valid @RequestBody JoinLeaveRoomRequest req) {
		return roomService.leave(roomId, req);
	}
}


