import { api } from "./client";

export type RoomType = "TYPING" | "DRAW" | "PAPER" | "ETC";
export type RoomStatus = "OPEN" | "CLOSED";

export type RoomSummary = {
  id: number;
  type: RoomType;
  status: RoomStatus;
  hostId: number;
  maxParticipants: number;
  participantCount: number;
};

export async function listRooms(): Promise<RoomSummary[]> {
  return api<RoomSummary[]>("/rooms");
}

export async function createRoom(input: {
  type: RoomType;
  hostId: number;
  maxParticipants: number;
}): Promise<RoomSummary> {
  return api<RoomSummary>("/rooms", {
    method: "POST",
    body: JSON.stringify(input),
  });
}

export async function joinRoom(roomId: number, userId: number): Promise<RoomSummary> {
  return api<RoomSummary>(`/rooms/${roomId}/join`, {
    method: "POST",
    body: JSON.stringify({ userId }),
  });
}

export async function leaveRoom(roomId: number, userId: number): Promise<RoomSummary> {
  return api<RoomSummary>(`/rooms/${roomId}/leave`, {
    method: "POST",
    body: JSON.stringify({ userId }),
  });
}


