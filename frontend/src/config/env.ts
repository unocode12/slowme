export const env = {
  apiBaseUrl: process.env.EXPO_PUBLIC_API_BASE_URL ?? "http://localhost:8080",
  wsUrl: process.env.EXPO_PUBLIC_WS_URL,
} as const;
