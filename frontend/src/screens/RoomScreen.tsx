import React, { useEffect, useRef, useState } from "react";
import { Alert, StyleSheet, Text, View } from "react-native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import type { RootStackParamList } from "../types/nav";
import { joinRoom, leaveRoom } from "../api/rooms";
import { Screen } from "../components/Screen";
import { TextField } from "../components/TextField";
import { Button } from "../components/Button";
import { colors } from "../theme/colors";
import { room as roomConst } from "../constants/room";
import { subscribeRoom } from "../realtime/rooms";

type Props = NativeStackScreenProps<RootStackParamList, "Room">;

export function RoomScreen({ route }: Props) {
  const roomId = route.params.roomId;
  const [userId, setUserId] = useState(String(roomConst.defaultUserId));
  const [info, setInfo] = useState<string>("입장/퇴장으로 상태를 확인하세요");
  const unsubscribeRef = useRef<null | (() => void)>(null);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const unsub = await subscribeRoom(roomId, (evt) => {
          if (!mounted) return;
          setInfo(
            `${evt.room.type} ${evt.room.status} · ${evt.room.participantCount}/${evt.room.maxParticipants}`,
          );
        });
        unsubscribeRef.current = unsub;
      } catch {
        // ignore
      }
    })();
    return () => {
      mounted = false;
      unsubscribeRef.current?.();
      unsubscribeRef.current = null;
    };
  }, [roomId]);

  async function onJoin() {
    try {
      const res = await joinRoom(roomId, Number(userId));
      setInfo(
        `${res.type} ${res.status} · ${res.participantCount}/${res.maxParticipants}`,
      );
    } catch (e: any) {
      Alert.alert("실패", e?.message ?? "입장 실패");
    }
  }

  async function onLeave() {
    try {
      const res = await leaveRoom(roomId, Number(userId));
      setInfo(
        `${res.type} ${res.status} · ${res.participantCount}/${res.maxParticipants}`,
      );
    } catch (e: any) {
      Alert.alert("실패", e?.message ?? "퇴장 실패");
    }
  }

  return (
    <Screen>
      <View style={styles.container}>
        <Text style={styles.title}>Room #{roomId}</Text>
        <Text style={styles.info}>{info}</Text>

        <TextField
          label="userId"
          value={userId}
          onChangeText={setUserId}
          keyboardType="number-pad"
        />

        <View style={{ flexDirection: "row", gap: 10, marginTop: 10 }}>
          <View style={{ flex: 1 }}>
            <Button title="입장" onPress={onJoin} />
          </View>
          <View style={{ flex: 1 }}>
            <Button title="퇴장" variant="secondary" onPress={onLeave} />
          </View>
        </View>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16 },
  title: { color: colors.text, fontSize: 20, fontWeight: "700" },
  info: { color: colors.muted, marginTop: 8, marginBottom: 16 },
});
