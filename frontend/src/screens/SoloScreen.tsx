import React, { useEffect, useMemo, useState } from "react";
import { Alert, StyleSheet, Text, View } from "react-native";
import { Audio } from "expo-av";
import { Screen } from "../components/Screen";
import { Button } from "../components/Button";
import { Chip } from "../components/Chip";
import { colors } from "../theme/colors";
import { asmr, type AsmrType } from "../constants/asmr";

export function SoloScreen() {
  const [type, setType] = useState<AsmrType>("TYPING");
  const [isPlaying, setIsPlaying] = useState(false);
  const [sound, setSound] = useState<Audio.Sound | null>(null);
  const uri = useMemo(() => asmr.sources[type], [type]);

  useEffect(() => {
    return () => {
      sound?.unloadAsync();
    };
  }, [sound]);

  async function toggle() {
    try {
      if (isPlaying && sound) {
        await sound.pauseAsync();
        setIsPlaying(false);
        return;
      }

      if (!sound) {
        await Audio.setAudioModeAsync({ playsInSilentModeIOS: true });
        const { sound: created } = await Audio.Sound.createAsync(
          { uri },
          { shouldPlay: true, isLooping: true },
        );
        setSound(created);
        setIsPlaying(true);
        return;
      }

      await sound.playAsync();
      setIsPlaying(true);
    } catch (e: any) {
      console.warn("[Solo] audio play failed", e);
      Alert.alert(
        "소리 재생 실패",
        e?.message ?? "오디오 로드/재생 중 오류가 발생했습니다.",
      );
    }
  }

  async function changeType(next: AsmrType) {
    setType(next);
    try {
      if (sound) {
        await sound.unloadAsync();
        setSound(null);
        setIsPlaying(false);
      }
    } catch (e: any) {
      console.warn("[Solo] audio unload failed", e);
    }
  }

  return (
    <Screen>
      <View style={styles.container}>
        <Text style={styles.title}>혼자 놀기</Text>
        <Text style={styles.hint}>타입 선택 → 재생</Text>

        <View style={styles.row}>
          {asmr.types.map((t) => (
            <Chip
              key={t}
              label={t}
              active={type === t}
              onPress={() => changeType(t)}
            />
          ))}
        </View>

        <View style={{ marginTop: 24 }}>
          <Button
            title={isPlaying ? "일시정지" : "재생"}
            variant="light"
            onPress={toggle}
          />
        </View>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 24, gap: 12 },
  title: { color: colors.text, fontSize: 20, fontWeight: "700" },
  hint: { color: colors.muted, fontSize: 13, marginBottom: 8 },
  row: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
});
