import React, { useState } from "react";
import { Alert, StyleSheet, Text, View } from "react-native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import type { RootStackParamList } from "../types/nav";
import { createRoom, RoomType } from "../api/rooms";
import { Screen } from "../components/Screen";
import { Chip } from "../components/Chip";
import { TextField } from "../components/TextField";
import { Button } from "../components/Button";
import { colors } from "../theme/colors";
import { room as roomConst } from "../constants/room";

type Props = NativeStackScreenProps<RootStackParamList, "CreateRoom">;

export function CreateRoomScreen({ navigation }: Props) {
  const [type, setType] = useState<RoomType>("TYPING");
  const [hostId, setHostId] = useState(String(roomConst.defaultHostId));
  const [max, setMax] = useState(String(roomConst.defaultMaxParticipants));

  async function onCreate() {
    try {
      const room = await createRoom({
        type,
        hostId: Number(hostId),
        maxParticipants: Number(max),
      });
      navigation.replace("Room", { roomId: room.id });
    } catch (e: any) {
      Alert.alert("실패", e?.message ?? "방 생성 실패");
    }
  }

  return (
    <Screen>
      <View style={styles.container}>
        <Text style={styles.title}>방 만들기</Text>

        <View style={styles.row}>
          {roomConst.types.map((t) => (
            <Chip
              key={t}
              label={t}
              active={type === t}
              onPress={() => setType(t)}
            />
          ))}
        </View>

        <TextField
          label="hostId"
          value={hostId}
          onChangeText={setHostId}
          keyboardType="number-pad"
        />
        <TextField
          label="maxParticipants"
          value={max}
          onChangeText={setMax}
          keyboardType="number-pad"
        />

        <View style={{ marginTop: 12 }}>
          <Button title="생성" variant="light" onPress={onCreate} />
        </View>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, gap: 10 },
  title: {
    color: colors.text,
    fontSize: 20,
    fontWeight: "700",
    marginBottom: 8,
  },
  row: { flexDirection: "row", flexWrap: "wrap", gap: 8, marginBottom: 10 },
});
