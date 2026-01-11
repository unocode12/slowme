import type { RoomType } from "../api/rooms";

export const room = {
  defaultHostId: 1,
  defaultUserId: 2,
  defaultMaxParticipants: 6,
  types: [
    "TYPING",
    "DRAW",
    "PAPER",
    "ETC",
  ] as const satisfies readonly RoomType[],
} as const;
