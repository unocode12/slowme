import React from "react";
import { Pressable, StyleSheet, Text } from "react-native";
import { colors } from "../theme/colors";

type Variant = "primary" | "secondary" | "light";

export function Button({
  title,
  onPress,
  variant = "primary",
}: {
  title: string;
  onPress: () => void;
  variant?: Variant;
}) {
  return (
    <Pressable style={[styles.base, styles[variant]]} onPress={onPress}>
      <Text style={[styles.text, variant === "light" && styles.textDark]}>
        {title}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    paddingVertical: 14,
    borderRadius: 14,
    alignItems: "center",
  },
  primary: { backgroundColor: colors.primary },
  secondary: { backgroundColor: colors.card2 },
  light: { backgroundColor: colors.text },
  text: { color: colors.text, fontSize: 16, fontWeight: "700" },
  textDark: { color: colors.onLight },
});
