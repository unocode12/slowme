import React, { useCallback, useEffect, useRef, useState } from "react";
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  StyleSheet,
  Text,
  View,
} from "react-native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import type { RootStackParamList } from "../types/nav";
import { listRooms, RoomSummary } from "../api/rooms";
import { Screen } from "../components/Screen";
import { Button } from "../components/Button";
import { colors } from "../theme/colors";
import { subscribeRooms } from "../realtime/rooms";

type Props = NativeStackScreenProps<RootStackParamList, "Rooms">;

export function RoomsScreen({ navigation }: Props) {
  const [items, setItems] = useState<RoomSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const unsubscribeRef = useRef<null | (() => void)>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await listRooms();
      setItems(data);
    } catch (e: any) {
      setError(e?.message ?? "불러오기 실패");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const unsubscribe = await subscribeRooms((evt) => {
          if (!mounted) return;
          setItems((prev) => {
            const idx = prev.findIndex((r) => r.id === evt.room.id);
            // CLOSED면 리스트에서 제거 (GET /rooms가 OPEN만 반환하므로 일관성 유지)
            if (evt.room.status === "CLOSED") {
              return prev.filter((r) => r.id !== evt.room.id);
            }
            if (idx === -1) return [evt.room, ...prev];
            const next = [...prev];
            next[idx] = evt.room;
            return next;
          });
        });
        unsubscribeRef.current = unsubscribe;
      } catch {
        // realtime이 없어도 기본 기능은 동작해야 하므로 조용히 무시
      }
    })();
    return () => {
      mounted = false;
      unsubscribeRef.current?.();
      unsubscribeRef.current = null;
    };
  }, []);

  return (
    <Screen>
      <View style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.title}>같이 놀기</Text>
          <View style={{ flexDirection: "row", gap: 8 }}>
            <View style={{ width: 92 }}>
              <Button title="새로고침" variant="secondary" onPress={load} />
            </View>
            <View style={{ width: 92 }}>
              <Button
                title="방 만들기"
                variant="secondary"
                onPress={() => navigation.navigate("CreateRoom")}
              />
            </View>
          </View>
        </View>

        {loading && <ActivityIndicator />}
        {error && <Text style={styles.error}>{error}</Text>}

        <FlatList
          data={items}
          keyExtractor={(it) => String(it.id)}
          contentContainerStyle={{ gap: 10, paddingTop: 12 }}
          renderItem={({ item }) => (
            <Pressable
              style={styles.card}
              onPress={() => navigation.navigate("Room", { roomId: item.id })}
            >
              <Text style={styles.cardTitle}>
                {item.type} · {item.participantCount}/{item.maxParticipants}
              </Text>
              <Text style={styles.cardSub}>hostId: {item.hostId}</Text>
            </Pressable>
          )}
          ListEmptyComponent={
            !loading ? (
              <Text style={styles.empty}>열려있는 방이 없어요</Text>
            ) : null
          }
        />
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16 },
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  title: { color: colors.text, fontSize: 20, fontWeight: "700" },
  error: { color: colors.danger, marginTop: 12 },
  card: {
    backgroundColor: colors.card,
    borderRadius: 14,
    padding: 14,
    borderWidth: 1,
    borderColor: colors.card2,
  },
  cardTitle: { color: colors.text, fontSize: 14, fontWeight: "700" },
  cardSub: { color: colors.muted, fontSize: 12, marginTop: 4 },
  empty: { color: colors.muted, marginTop: 24, textAlign: "center" },
});
