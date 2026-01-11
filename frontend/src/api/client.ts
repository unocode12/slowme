import { env } from "../config/env";

export const API_BASE_URL = env.apiBaseUrl;

export class ApiError extends Error {
  constructor(
    message: string,
    public readonly status: number,
    public readonly requestId?: string,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

function safeJsonParse(text: string): any | null {
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

export async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  const text = await res.text();
  const json = text ? safeJsonParse(text) : null;

  if (!res.ok) {
    const message =
      (json && (json.message as string)) || `${res.status} ${res.statusText}`;
    const requestId = res.headers.get("X-Request-Id") ?? undefined;
    throw new ApiError(message, res.status, requestId);
  }

  return json as T;
}
