package mchorse.bbs_mod.network.payloads;

import mchorse.bbs_mod.BBSMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

/**
 * Central registry for all BBS network payloads.
 * In 1.21.1, networking uses CustomPayload records with PacketCodecs
 * instead of raw PacketByteBuf + Identifier.
 */
public class BBSPayloads
{
    // ========== CLIENT-BOUND PAYLOADS (Server -> Client) ==========

    /**
     * Sent when a player clicks a model block
     */
    public record ClickedModelBlockPayload(BlockPos pos) implements CustomPayload
    {
        public static final Id<ClickedModelBlockPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "clicked_model_block"));
        public static final PacketCodec<RegistryByteBuf, ClickedModelBlockPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, ClickedModelBlockPayload::pos,
            ClickedModelBlockPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Sent to sync player form/morph data
     */
    public record PlayerFormPayload(int playerId, byte[] formData) implements CustomPayload
    {
        public static final Id<PlayerFormPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "player_form"));
        public static final PacketCodec<RegistryByteBuf, PlayerFormPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, PlayerFormPayload::playerId,
            PacketCodecs.BYTE_ARRAY, PlayerFormPayload::formData,
            PlayerFormPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Sent to play a film
     */
    public record PlayFilmPayload(String filmId, byte[] filmData, boolean withCamera) implements CustomPayload
    {
        public static final Id<PlayFilmPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "play_film"));
        public static final PacketCodec<RegistryByteBuf, PlayFilmPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, PlayFilmPayload::filmId,
            PacketCodecs.BYTE_ARRAY, PlayFilmPayload::filmData,
            PacketCodecs.BOOL, PlayFilmPayload::withCamera,
            PlayFilmPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Sent to stop a film
     */
    public record StopFilmPayload(String filmId) implements CustomPayload
    {
        public static final Id<StopFilmPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "stop_film"));
        public static final PacketCodec<RegistryByteBuf, StopFilmPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, StopFilmPayload::filmId,
            StopFilmPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Sent to pause a film
     */
    public record PauseFilmPayload(String filmId) implements CustomPayload
    {
        public static final Id<PauseFilmPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "pause_film"));
        public static final PacketCodec<RegistryByteBuf, PauseFilmPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, PauseFilmPayload::filmId,
            PauseFilmPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Manager data response (load/save/keys operations)
     */
    public record ManagerDataPayload(int callbackId, int operation, byte[] data) implements CustomPayload
    {
        public static final Id<ManagerDataPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "manager_data"));
        public static final PacketCodec<RegistryByteBuf, ManagerDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ManagerDataPayload::callbackId,
            PacketCodecs.INTEGER, ManagerDataPayload::operation,
            PacketCodecs.BYTE_ARRAY, ManagerDataPayload::data,
            ManagerDataPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Handshake packet for client connection
     */
    public record HandshakePayload(String serverId) implements CustomPayload
    {
        public static final Id<HandshakePayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "handshake"));
        public static final PacketCodec<RegistryByteBuf, HandshakePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, HandshakePayload::serverId,
            HandshakePayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Recorded actions from server
     */
    public record RecordedActionsPayload(String filmId, int replayId, int tick, byte[] clipsData) implements CustomPayload
    {
        public static final Id<RecordedActionsPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "recorded_actions"));
        public static final PacketCodec<RegistryByteBuf, RecordedActionsPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, RecordedActionsPayload::filmId,
            PacketCodecs.INTEGER, RecordedActionsPayload::replayId,
            PacketCodecs.INTEGER, RecordedActionsPayload::tick,
            PacketCodecs.BYTE_ARRAY, RecordedActionsPayload::clipsData,
            RecordedActionsPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Animation state trigger
     */
    public record AnimationStateTriggerPayload(int entityId, String trigger, int type) implements CustomPayload
    {
        public static final Id<AnimationStateTriggerPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "animation_state_trigger"));
        public static final PacketCodec<RegistryByteBuf, AnimationStateTriggerPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, AnimationStateTriggerPayload::entityId,
            PacketCodecs.STRING, AnimationStateTriggerPayload::trigger,
            PacketCodecs.INTEGER, AnimationStateTriggerPayload::type,
            AnimationStateTriggerPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Cheats permission notification
     */
    public record CheatsPermissionPayload(boolean allowed) implements CustomPayload
    {
        public static final Id<CheatsPermissionPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "cheats_permission"));
        public static final PacketCodec<RegistryByteBuf, CheatsPermissionPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, CheatsPermissionPayload::allowed,
            CheatsPermissionPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Shared form between players
     */
    public record SharedFormPayload(byte[] formData) implements CustomPayload
    {
        public static final Id<SharedFormPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "shared_form"));
        public static final PacketCodec<RegistryByteBuf, SharedFormPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BYTE_ARRAY, SharedFormPayload::formData,
            SharedFormPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Entity form sync
     */
    public record EntityFormPayload(int entityId, byte[] formData) implements CustomPayload
    {
        public static final Id<EntityFormPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "entity_form"));
        public static final PacketCodec<RegistryByteBuf, EntityFormPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, EntityFormPayload::entityId,
            PacketCodecs.BYTE_ARRAY, EntityFormPayload::formData,
            EntityFormPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Gun properties for projectile
     */
    public record GunPropertiesPayload(int entityId, byte[] propertiesData) implements CustomPayload
    {
        public static final Id<GunPropertiesPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "gun_properties"));
        public static final PacketCodec<RegistryByteBuf, GunPropertiesPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, GunPropertiesPayload::entityId,
            PacketCodecs.BYTE_ARRAY, GunPropertiesPayload::propertiesData,
            GunPropertiesPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Selected inventory slot sync
     */
    public record SelectedSlotPayload(int slot) implements CustomPayload
    {
        public static final Id<SelectedSlotPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "selected_slot"));
        public static final PacketCodec<RegistryByteBuf, SelectedSlotPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SelectedSlotPayload::slot,
            SelectedSlotPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Model block animation state trigger
     */
    public record ModelBlockStateTriggerPayload(BlockPos pos, String trigger) implements CustomPayload
    {
        public static final Id<ModelBlockStateTriggerPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "model_block_state_trigger"));
        public static final PacketCodec<RegistryByteBuf, ModelBlockStateTriggerPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, ModelBlockStateTriggerPayload::pos,
            PacketCodecs.STRING, ModelBlockStateTriggerPayload::trigger,
            ModelBlockStateTriggerPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Refresh model blocks
     */
    public record RefreshModelBlocksPayload(int tickRandom) implements CustomPayload
    {
        public static final Id<RefreshModelBlocksPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "refresh_model_blocks"));
        public static final PacketCodec<RegistryByteBuf, RefreshModelBlocksPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, RefreshModelBlocksPayload::tickRandom,
            RefreshModelBlocksPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Actors list for film
     */
    public record ActorsPayload(String filmId, int[] entityIds, String[] actorKeys) implements CustomPayload
    {
        public static final Id<ActorsPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "actors"));
        public static final PacketCodec<RegistryByteBuf, ActorsPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeString(value.filmId);
                buf.writeInt(value.entityIds.length);
                for (int i = 0; i < value.entityIds.length; i++) {
                    buf.writeString(value.actorKeys[i]);
                    buf.writeInt(value.entityIds[i]);
                }
            },
            buf -> {
                String filmId = buf.readString();
                int count = buf.readInt();
                String[] keys = new String[count];
                int[] ids = new int[count];
                for (int i = 0; i < count; i++) {
                    keys[i] = buf.readString();
                    ids[i] = buf.readInt();
                }
                return new ActorsPayload(filmId, ids, keys);
            }
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    // ========== SERVER-BOUND PAYLOADS (Client -> Server) ==========

    /**
     * Request to update model block form
     */
    public record ModelBlockFormRequestPayload(BlockPos pos, byte[] formData) implements CustomPayload
    {
        public static final Id<ModelBlockFormRequestPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "model_block_form_request"));
        public static final PacketCodec<RegistryByteBuf, ModelBlockFormRequestPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, ModelBlockFormRequestPayload::pos,
            PacketCodecs.BYTE_ARRAY, ModelBlockFormRequestPayload::formData,
            ModelBlockFormRequestPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Request to update model block transforms (item in hand)
     */
    public record ModelBlockTransformsRequestPayload(byte[] transformsData) implements CustomPayload
    {
        public static final Id<ModelBlockTransformsRequestPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "model_block_transforms_request"));
        public static final PacketCodec<RegistryByteBuf, ModelBlockTransformsRequestPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BYTE_ARRAY, ModelBlockTransformsRequestPayload::transformsData,
            ModelBlockTransformsRequestPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Request to update player form
     */
    public record PlayerFormRequestPayload(byte[] formData) implements CustomPayload
    {
        public static final Id<PlayerFormRequestPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "player_form_request"));
        public static final PacketCodec<RegistryByteBuf, PlayerFormRequestPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BYTE_ARRAY, PlayerFormRequestPayload::formData,
            PlayerFormRequestPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Manager data request (load/save/keys operations)
     */
    public record ManagerDataRequestPayload(int callbackId, int operation, byte[] data) implements CustomPayload
    {
        public static final Id<ManagerDataRequestPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "manager_data_request"));
        public static final PacketCodec<RegistryByteBuf, ManagerDataRequestPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ManagerDataRequestPayload::callbackId,
            PacketCodecs.INTEGER, ManagerDataRequestPayload::operation,
            PacketCodecs.BYTE_ARRAY, ManagerDataRequestPayload::data,
            ManagerDataRequestPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Action recording start/stop
     */
    public record ActionRecordingPayload(String filmId, int replayId, int tick, int countdown, boolean recording) implements CustomPayload
    {
        public static final Id<ActionRecordingPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "action_recording"));
        public static final PacketCodec<RegistryByteBuf, ActionRecordingPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, ActionRecordingPayload::filmId,
            PacketCodecs.INTEGER, ActionRecordingPayload::replayId,
            PacketCodecs.INTEGER, ActionRecordingPayload::tick,
            PacketCodecs.INTEGER, ActionRecordingPayload::countdown,
            PacketCodecs.BOOL, ActionRecordingPayload::recording,
            ActionRecordingPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Toggle film playback
     */
    public record ToggleFilmPayload(String filmId, boolean withCamera) implements CustomPayload
    {
        public static final Id<ToggleFilmPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "toggle_film"));
        public static final PacketCodec<RegistryByteBuf, ToggleFilmPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, ToggleFilmPayload::filmId,
            PacketCodecs.BOOL, ToggleFilmPayload::withCamera,
            ToggleFilmPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Action control (seek, play, pause, restart, stop)
     */
    public record ActionControlPayload(String filmId, byte state, int tick) implements CustomPayload
    {
        public static final Id<ActionControlPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "action_control"));
        public static final PacketCodec<RegistryByteBuf, ActionControlPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, ActionControlPayload::filmId,
            PacketCodecs.BYTE, ActionControlPayload::state,
            PacketCodecs.INTEGER, ActionControlPayload::tick,
            ActionControlPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Film data sync (partial update)
     */
    public record FilmDataSyncPayload(String filmId, String[] path, byte[] data) implements CustomPayload
    {
        public static final Id<FilmDataSyncPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "film_data_sync"));
        public static final PacketCodec<RegistryByteBuf, FilmDataSyncPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeString(value.filmId);
                buf.writeInt(value.path.length);
                for (String s : value.path) {
                    buf.writeString(s);
                }
                buf.writeByteArray(value.data);
            },
            buf -> {
                String filmId = buf.readString();
                int pathLen = buf.readInt();
                String[] path = new String[pathLen];
                for (int i = 0; i < pathLen; i++) {
                    path[i] = buf.readString();
                }
                byte[] data = buf.readByteArray();
                return new FilmDataSyncPayload(filmId, path, data);
            }
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Player teleport request
     */
    public record PlayerTeleportPayload(double x, double y, double z, float yaw, float bodyYaw, float pitch) implements CustomPayload
    {
        public static final Id<PlayerTeleportPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "player_teleport"));
        public static final PacketCodec<RegistryByteBuf, PlayerTeleportPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeDouble(value.x);
                buf.writeDouble(value.y);
                buf.writeDouble(value.z);
                buf.writeFloat(value.yaw);
                buf.writeFloat(value.bodyYaw);
                buf.writeFloat(value.pitch);
            },
            buf -> new PlayerTeleportPayload(
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readFloat(), buf.readFloat(), buf.readFloat()
            )
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Animation state trigger from client
     */
    public record AnimationStateTriggerRequestPayload(String trigger, int type) implements CustomPayload
    {
        public static final Id<AnimationStateTriggerRequestPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "animation_state_trigger_request"));
        public static final PacketCodec<RegistryByteBuf, AnimationStateTriggerRequestPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, AnimationStateTriggerRequestPayload::trigger,
            PacketCodecs.INTEGER, AnimationStateTriggerRequestPayload::type,
            AnimationStateTriggerRequestPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Share form with another player
     */
    public record ShareFormPayload(java.util.UUID targetPlayer, byte[] formData) implements CustomPayload
    {
        public static final Id<ShareFormPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "share_form"));
        public static final PacketCodec<RegistryByteBuf, ShareFormPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, ShareFormPayload::targetPlayer,
            PacketCodecs.BYTE_ARRAY, ShareFormPayload::formData,
            ShareFormPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Zoom toggle for gun
     */
    public record ZoomPayload(boolean zooming) implements CustomPayload
    {
        public static final Id<ZoomPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "zoom"));
        public static final PacketCodec<RegistryByteBuf, ZoomPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, ZoomPayload::zooming,
            ZoomPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    /**
     * Pause film request
     */
    public record PauseFilmRequestPayload(String filmId) implements CustomPayload
    {
        public static final Id<PauseFilmRequestPayload> ID = new Id<>(Identifier.of(BBSMod.MOD_ID, "pause_film_request"));
        public static final PacketCodec<RegistryByteBuf, PauseFilmRequestPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, PauseFilmRequestPayload::filmId,
            PauseFilmRequestPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    // ========== REGISTRATION ==========

    /**
     * Register all payload types. Must be called during mod initialization
     * BEFORE registering any handlers.
     */
    public static void registerPayloads()
    {
        // Client-bound payloads (S2C)
        PayloadTypeRegistry.playS2C().register(ClickedModelBlockPayload.ID, ClickedModelBlockPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerFormPayload.ID, PlayerFormPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayFilmPayload.ID, PlayFilmPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StopFilmPayload.ID, StopFilmPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PauseFilmPayload.ID, PauseFilmPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ManagerDataPayload.ID, ManagerDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(HandshakePayload.ID, HandshakePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RecordedActionsPayload.ID, RecordedActionsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AnimationStateTriggerPayload.ID, AnimationStateTriggerPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CheatsPermissionPayload.ID, CheatsPermissionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SharedFormPayload.ID, SharedFormPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityFormPayload.ID, EntityFormPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GunPropertiesPayload.ID, GunPropertiesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SelectedSlotPayload.ID, SelectedSlotPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ModelBlockStateTriggerPayload.ID, ModelBlockStateTriggerPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RefreshModelBlocksPayload.ID, RefreshModelBlocksPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ActorsPayload.ID, ActorsPayload.CODEC);

        // Server-bound payloads (C2S)
        PayloadTypeRegistry.playC2S().register(ModelBlockFormRequestPayload.ID, ModelBlockFormRequestPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ModelBlockTransformsRequestPayload.ID, ModelBlockTransformsRequestPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerFormRequestPayload.ID, PlayerFormRequestPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ManagerDataRequestPayload.ID, ManagerDataRequestPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ActionRecordingPayload.ID, ActionRecordingPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleFilmPayload.ID, ToggleFilmPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ActionControlPayload.ID, ActionControlPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(FilmDataSyncPayload.ID, FilmDataSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerTeleportPayload.ID, PlayerTeleportPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AnimationStateTriggerRequestPayload.ID, AnimationStateTriggerRequestPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ShareFormPayload.ID, ShareFormPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ZoomPayload.ID, ZoomPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PauseFilmRequestPayload.ID, PauseFilmRequestPayload.CODEC);
    }
}
