export type AsmrType = "TYPING" | "DRAW" | "PAPER" | "ETC";

export const asmr = {
  types: [
    "TYPING",
    "DRAW",
    "PAPER",
    "ETC",
  ] as const satisfies readonly AsmrType[],
  // NOTE: MVP용 임시 URL.
  // 기존 OGG 소스는 웹/환경에 따라 CORS/코덱 이슈로 재생이 안 될 수 있어서,
  // 웹/모바일에서 대체로 잘 동작하는 MP3 샘플로 맞춰둠.
  sources: {
    TYPING:
      "https://interactive-examples.mdn.mozilla.net/media/cc0-audio/t-rex-roar.mp3",
    DRAW: "https://interactive-examples.mdn.mozilla.net/media/cc0-audio/t-rex-roar.mp3",
    PAPER:
      "https://interactive-examples.mdn.mozilla.net/media/cc0-audio/t-rex-roar.mp3",
    ETC: "https://interactive-examples.mdn.mozilla.net/media/cc0-audio/t-rex-roar.mp3",
  } as const satisfies Record<AsmrType, string>,
} as const;
