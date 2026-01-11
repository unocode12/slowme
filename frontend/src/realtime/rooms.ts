import { Client } from "@stomp/stompjs";
import { env } from "../config/env";
import type { RoomSummary } from "../api/rooms";

export type RoomEventType =
  | "ROOM_CREATED"
  | "USER_JOINED"
  | "USER_LEFT"
  | "ROOM_CLOSED";

export type RoomRealtimeEvent = {
  type: RoomEventType;
  room: RoomSummary;
};

function deriveWsUrl(): string {
  if (env.wsUrl) return env.wsUrl;
  // http(s) -> ws(s)
  const base = env.apiBaseUrl.replace(/^http/, "ws");
  return `${base}/ws`;
}

let client: Client | null = null;
let connected = false;
let connecting: Promise<void> | null = null;

async function ensureConnected(): Promise<Client> {
  if (client && connected) return client;
  if (client && connecting) {
    await connecting;
    return client;
  }

  const url = deriveWsUrl();
  client = new Client({
    brokerURL: url,
    reconnectDelay: 2000,
    debug: () => {},
  });

  connecting = new Promise<void>((resolve, reject) => {
    client!.onConnect = () => {
      connected = true;
      resolve();
    };
    client!.onStompError = (frame) => {
      reject(new Error(frame.headers["message"] ?? "STOMP error"));
    };
    client!.onWebSocketError = (evt) => {
      reject(new Error("WebSocket error"));
    };
  });

  client.activate();
  await connecting;
  connecting = null;
  return client!;
}

export async function subscribeRooms(
  onEvent: (event: RoomRealtimeEvent) => void,
): Promise<() => void> {
  const c = await ensureConnected();
  const sub = c.subscribe("/topic/rooms", (msg) => {
    try {
      const parsed = JSON.parse(msg.body) as RoomRealtimeEvent;
      onEvent(parsed);
    } catch {
      // ignore
    }
  });
  return () => sub.unsubscribe();
}

export async function subscribeRoom(
  roomId: number,
  onEvent: (event: RoomRealtimeEvent) => void,
): Promise<() => void> {
  const c = await ensureConnected();
  const sub = c.subscribe(`/topic/rooms.${roomId}`, (msg) => {
    try {
      const parsed = JSON.parse(msg.body) as RoomRealtimeEvent;
      onEvent(parsed);
    } catch {
      // ignore
    }
  });
  return () => sub.unsubscribe();
}


