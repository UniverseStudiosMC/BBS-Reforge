package mchorse.bbs_mod.network;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.actions.ActionManager;
import mchorse.bbs_mod.actions.ActionPlayer;
import mchorse.bbs_mod.actions.ActionRecorder;
import mchorse.bbs_mod.actions.ActionState;
import mchorse.bbs_mod.actions.PlayerType;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.data.DataStorageUtils;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.ByteType;
import mchorse.bbs_mod.data.types.ListType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.entity.GunProjectileEntity;
import mchorse.bbs_mod.entity.IEntityFormProvider;
import mchorse.bbs_mod.film.Film;
import mchorse.bbs_mod.film.FilmManager;
import mchorse.bbs_mod.forms.FormUtils;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.items.GunProperties;
import mchorse.bbs_mod.morphing.Morph;
import mchorse.bbs_mod.network.payloads.BBSPayloads;
import mchorse.bbs_mod.network.payloads.BBSPayloads.*;
import mchorse.bbs_mod.utils.DataPath;
import mchorse.bbs_mod.utils.EnumUtils;
import mchorse.bbs_mod.utils.PermissionUtils;
import mchorse.bbs_mod.utils.clips.Clips;
import mchorse.bbs_mod.utils.repos.RepositoryOperation;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServerNetwork
{
    public static final int STATE_TRIGGER_MORPH = 0;
    public static final int STATE_TRIGGER_MAIN_HAND_ITEM = 1;
    public static final int STATE_TRIGGER_OFF_HAND_ITEM = 2;

    public static void reset()
    {
        // No crusher to reset in the new system
    }

    public static void setup()
    {
        // Register all payloads first
        BBSPayloads.registerPayloads();

        // Register C2S (client-to-server) handlers
        ServerPlayNetworking.registerGlobalReceiver(ModelBlockFormRequestPayload.ID, ServerNetwork::handleModelBlockFormPacket);
        ServerPlayNetworking.registerGlobalReceiver(ModelBlockTransformsRequestPayload.ID, ServerNetwork::handleModelBlockTransformsPacket);
        ServerPlayNetworking.registerGlobalReceiver(PlayerFormRequestPayload.ID, ServerNetwork::handlePlayerFormPacket);
        ServerPlayNetworking.registerGlobalReceiver(ManagerDataRequestPayload.ID, ServerNetwork::handleManagerDataPacket);
        ServerPlayNetworking.registerGlobalReceiver(ActionRecordingPayload.ID, ServerNetwork::handleActionRecording);
        ServerPlayNetworking.registerGlobalReceiver(ToggleFilmPayload.ID, ServerNetwork::handleToggleFilm);
        ServerPlayNetworking.registerGlobalReceiver(ActionControlPayload.ID, ServerNetwork::handleActionControl);
        ServerPlayNetworking.registerGlobalReceiver(FilmDataSyncPayload.ID, ServerNetwork::handleSyncData);
        ServerPlayNetworking.registerGlobalReceiver(PlayerTeleportPayload.ID, ServerNetwork::handleTeleportPlayer);
        ServerPlayNetworking.registerGlobalReceiver(AnimationStateTriggerRequestPayload.ID, ServerNetwork::handleAnimationStateTriggerPacket);
        ServerPlayNetworking.registerGlobalReceiver(ShareFormPayload.ID, ServerNetwork::handleSharedFormPacket);
        ServerPlayNetworking.registerGlobalReceiver(ZoomPayload.ID, ServerNetwork::handleZoomPacket);
        ServerPlayNetworking.registerGlobalReceiver(PauseFilmRequestPayload.ID, ServerNetwork::handlePauseFilmPacket);
    }

    /* Handlers */

    private static void handleModelBlockFormPacket(ModelBlockFormRequestPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        BlockPos pos = payload.pos();
        byte[] bytes = payload.formData();

        try
        {
            MapType data = (MapType) DataStorageUtils.readFromBytes(bytes);

            server.execute(() ->
            {
                World world = player.getWorld();
                BlockEntity be = world.getBlockEntity(pos);

                if (be instanceof ModelBlockEntity modelBlock)
                {
                    modelBlock.updateForm(data, world);
                }
            });
        }
        catch (Exception e)
        {}
    }

    private static void handleModelBlockTransformsPacket(ModelBlockTransformsRequestPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        byte[] bytes = payload.transformsData();

        try
        {
            MapType data = (MapType) DataStorageUtils.readFromBytes(bytes);

            server.execute(() ->
            {
                ItemStack stack = player.getEquippedStack(EquipmentSlot.MAINHAND).copy();

                // TODO: Update to use Data Components instead of NBT
                // if (stack.getItem() == BBSMod.MODEL_BLOCK_ITEM) stack.getNbt().getCompound("BlockEntityTag").put("Properties", DataStorageUtils.toNbt(data));
                // else if (stack.getItem() == BBSMod.GUN_ITEM) stack.getOrCreateNbt().put("GunData", DataStorageUtils.toNbt(data));

                player.equipStack(EquipmentSlot.MAINHAND, stack);
            });
        }
        catch (Exception e)
        {}
    }

    private static void handlePlayerFormPacket(PlayerFormRequestPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        byte[] bytes = payload.formData();
        Form form = null;

        try
        {
            if (DataStorageUtils.readFromBytes(bytes) instanceof MapType data)
            {
                form = BBSMod.getForms().fromData(data);
            }
        }
        catch (Exception e)
        {}

        final Form finalForm = form;

        server.execute(() ->
        {
            Morph.getMorph(player).setForm(FormUtils.copy(finalForm));

            sendMorphToTracked(player, finalForm);
        });
    }

    private static void handleManagerDataPacket(ManagerDataRequestPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        byte[] bytes = payload.data();
        int callbackId = payload.callbackId();
        RepositoryOperation op = RepositoryOperation.values()[payload.operation()];

        MapType data = (MapType) DataStorageUtils.readFromBytes(bytes);
        FilmManager films = BBSMod.getFilms();

        if (op == RepositoryOperation.LOAD)
        {
            String id = data.getString("id");
            Film film = films.load(id);

            sendManagerData(player, callbackId, op, film.toData());
        }
        else if (op == RepositoryOperation.SAVE)
        {
            films.save(data.getString("id"), data.getMap("data"));
        }
        else if (op == RepositoryOperation.RENAME)
        {
            films.rename(data.getString("from"), data.getString("to"));
        }
        else if (op == RepositoryOperation.DELETE)
        {
            films.delete(data.getString("id"));
        }
        else if (op == RepositoryOperation.KEYS)
        {
            ListType list = DataStorageUtils.stringListToData(films.getKeys());

            sendManagerData(player, callbackId, op, list);
        }
        else if (op == RepositoryOperation.ADD_FOLDER)
        {
            sendManagerData(player, callbackId, op, new ByteType(films.addFolder(data.getString("folder"))));
        }
        else if (op == RepositoryOperation.RENAME_FOLDER)
        {
            sendManagerData(player, callbackId, op, new ByteType(films.renameFolder(data.getString("from"), data.getString("to"))));
        }
        else if (op == RepositoryOperation.DELETE_FOLDER)
        {
            sendManagerData(player, callbackId, op, new ByteType(films.deleteFolder(data.getString("folder"))));
        }
    }

    private static void handleActionRecording(ActionRecordingPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        String filmId = payload.filmId();
        int replayId = payload.replayId();
        int tick = payload.tick();
        int countdown = payload.countdown();
        boolean recording = payload.recording();

        server.execute(() ->
        {
            if (recording)
            {
                Film film = BBSMod.getFilms().load(filmId);

                if (film != null)
                {
                    BBSMod.getActions().startRecording(film, player, 0, countdown, replayId);
                }
            }
            else
            {
                ActionRecorder recorder = BBSMod.getActions().stopRecording(player);
                Clips clips = recorder.composeClips();

                /* Send recorded clips to the client */
                sendRecordedActions(player, filmId, replayId, tick, clips);
            }
        });
    }

    private static void handleToggleFilm(ToggleFilmPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        String filmId = payload.filmId();
        boolean withCamera = payload.withCamera();

        server.execute(() ->
        {
            ActionPlayer actionPlayer = BBSMod.getActions().getPlayer(filmId);

            if (actionPlayer != null)
            {
                BBSMod.getActions().stop(filmId);

                for (ServerPlayerEntity otherPlayer : server.getPlayerManager().getPlayerList())
                {
                    sendStopFilm(otherPlayer, filmId);
                }
            }
            else
            {
                sendPlayFilm(player, player.getServerWorld(), filmId, withCamera);
            }
        });
    }

    private static void handleActionControl(ActionControlPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        ActionManager actions = BBSMod.getActions();
        String filmId = payload.filmId();
        ActionState state = EnumUtils.getValue(payload.state(), ActionState.values(), ActionState.STOP);
        int tick = payload.tick();

        server.execute(() ->
        {
            if (state == ActionState.SEEK)
            {
                ActionPlayer actionPlayer = actions.getPlayer(filmId);

                if (actionPlayer != null)
                {
                    actionPlayer.goTo(tick);
                }
            }
            else if (state == ActionState.PLAY)
            {
                ActionPlayer actionPlayer = actions.getPlayer(filmId);

                if (actionPlayer != null)
                {
                    actionPlayer.goTo(tick);
                    actionPlayer.playing = true;
                }
            }
            else if (state == ActionState.PAUSE)
            {
                ActionPlayer actionPlayer = actions.getPlayer(filmId);

                if (actionPlayer != null)
                {
                    actionPlayer.goTo(tick);
                    actionPlayer.playing = false;
                }
            }
            else if (state == ActionState.RESTART)
            {
                ActionPlayer actionPlayer = actions.getPlayer(filmId);

                if (actionPlayer == null)
                {
                    Film film = BBSMod.getFilms().load(filmId);

                    if (film != null)
                    {
                        actionPlayer = actions.play(player, player.getServerWorld(), film, tick, PlayerType.FILM_EDITOR);
                    }
                }
                else
                {
                    actions.stop(filmId);

                    actionPlayer = actions.play(player, player.getServerWorld(), actionPlayer.film, tick, PlayerType.FILM_EDITOR);
                }

                if (actionPlayer != null)
                {
                    actionPlayer.syncing = true;
                    actionPlayer.playing = false;

                    if (tick != 0)
                    {
                        actionPlayer.goTo(0, tick);
                    }
                }

                sendStopFilm(player, filmId);
            }
            else if (state == ActionState.STOP)
            {
                actions.stop(filmId);
            }
        });
    }

    private static void handleSyncData(FilmDataSyncPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        String filmId = payload.filmId();
        String[] pathArray = payload.path();
        byte[] bytes = payload.data();

        List<String> path = new ArrayList<>();
        for (String s : pathArray)
        {
            path.add(s);
        }

        BaseType data = DataStorageUtils.readFromBytes(bytes);

        server.execute(() ->
        {
            BBSMod.getActions().syncData(filmId, new DataPath(path), data);
        });
    }

    private static void handleTeleportPlayer(PlayerTeleportPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        if (!PermissionUtils.arePanelsAllowed(server, player))
        {
            return;
        }

        double x = payload.x();
        double y = payload.y();
        double z = payload.z();
        float yaw = payload.yaw();
        float bodyYaw = payload.bodyYaw();
        float pitch = payload.pitch();

        server.execute(() ->
        {
            player.requestTeleport(x, y, z);

            player.setYaw(yaw);
            player.setHeadYaw(yaw);
            player.setBodyYaw(bodyYaw);
            player.setPitch(pitch);
        });
    }

    private static void handleAnimationStateTriggerPacket(AnimationStateTriggerRequestPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        String trigger = payload.trigger();
        int type = payload.type();

        AnimationStateTriggerPayload outPayload = new AnimationStateTriggerPayload(player.getId(), trigger, type);

        for (ServerPlayerEntity otherPlayer : PlayerLookup.tracking(player))
        {
            ServerPlayNetworking.send(otherPlayer, outPayload);
        }

        server.execute(() ->
        {
            /* TODO: State Triggers */
        });
    }

    private static void handleSharedFormPacket(ShareFormPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        UUID playerUuid = payload.targetPlayer();
        byte[] bytes = payload.formData();
        MapType data = (MapType) DataStorageUtils.readFromBytes(bytes);

        server.execute(() ->
        {
            ServerPlayerEntity otherPlayer = server.getPlayerManager().getPlayer(playerUuid);

            if (otherPlayer != null)
            {
                sendSharedForm(otherPlayer, data);
            }
        });
    }

    private static void handleZoomPacket(ZoomPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        boolean zoom = payload.zooming();
        ItemStack main = player.getMainHandStack();

        if (main.getItem() == BBSMod.GUN_ITEM)
        {
            GunProperties properties = GunProperties.get(main);
            String command = zoom ? properties.cmdZoomOn : properties.cmdZoomOff;

            if (!command.isEmpty())
            {
                server.getCommandManager().executeWithPrefix(player.getCommandSource(), command);
            }
        }
    }

    private static void handlePauseFilmPacket(PauseFilmRequestPayload payload, ServerPlayNetworking.Context context)
    {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();

        String filmId = payload.filmId();

        ActionPlayer actionPlayer = BBSMod.getActions().getPlayer(filmId);

        if (actionPlayer != null)
        {
            actionPlayer.toggle();
        }

        for (ServerPlayerEntity playerEntity : server.getPlayerManager().getPlayerList())
        {
            sendPauseFilm(playerEntity, filmId);
        }
    }

    /* API */

    public static void sendMorph(ServerPlayerEntity player, int playerId, Form form)
    {
        byte[] formData = DataStorageUtils.writeToBytes(FormUtils.toData(form));
        ServerPlayNetworking.send(player, new PlayerFormPayload(playerId, formData));
    }

    public static void sendMorphToTracked(ServerPlayerEntity player, Form form)
    {
        sendMorph(player, player.getId(), form);

        for (ServerPlayerEntity otherPlayer : PlayerLookup.tracking(player))
        {
            sendMorph(otherPlayer, player.getId(), form);
        }
    }

    public static void sendClickedModelBlock(ServerPlayerEntity player, BlockPos pos)
    {
        ServerPlayNetworking.send(player, new ClickedModelBlockPayload(pos));
    }

    public static void sendPlayFilm(ServerPlayerEntity player, ServerWorld world, String filmId, boolean withCamera)
    {
        try
        {
            Film film = BBSMod.getFilms().load(filmId);

            if (film != null)
            {
                BBSMod.getActions().play(player, world, film, 0);

                BaseType data = film.toData();
                byte[] filmData = DataStorageUtils.writeToBytes(data);

                for (PlayerEntity p : world.getPlayers())
                {
                    if (p instanceof ServerPlayerEntity serverPlayer)
                    {
                        ServerPlayNetworking.send(serverPlayer, new PlayFilmPayload(filmId, filmData, withCamera));
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void sendPlayFilm(ServerPlayerEntity player, String filmId, boolean withCamera)
    {
        try
        {
            Film film = BBSMod.getFilms().load(filmId);

            if (film != null)
            {
                BBSMod.getActions().play(player, player.getServerWorld(), film, 0);

                byte[] filmData = DataStorageUtils.writeToBytes(film.toData());
                ServerPlayNetworking.send(player, new PlayFilmPayload(filmId, filmData, withCamera));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void sendStopFilm(ServerPlayerEntity player, String filmId)
    {
        ServerPlayNetworking.send(player, new StopFilmPayload(filmId));
    }

    public static void sendManagerData(ServerPlayerEntity player, int callbackId, RepositoryOperation op, BaseType data)
    {
        byte[] bytes = DataStorageUtils.writeToBytes(data);
        ServerPlayNetworking.send(player, new ManagerDataPayload(callbackId, op.ordinal(), bytes));
    }

    public static void sendRecordedActions(ServerPlayerEntity player, String filmId, int replayId, int tick, Clips clips)
    {
        byte[] clipsData = DataStorageUtils.writeToBytes(clips.toData());
        ServerPlayNetworking.send(player, new RecordedActionsPayload(filmId, replayId, tick, clipsData));
    }

    public static void sendHandshake(MinecraftServer server, ServerPlayNetworkHandler handler)
    {
        String id = "";

        /* No need to do that in singleplayer */
        if (server.isSingleplayer())
        {
            id = "";
        }

        ServerPlayNetworking.send(handler.player, new HandshakePayload(id));
    }

    public static void sendHandshake(MinecraftServer server, ServerPlayerEntity player)
    {
        String id = "";

        /* No need to do that in singleplayer */
        if (server.isSingleplayer())
        {
            id = "";
        }

        ServerPlayNetworking.send(player, new HandshakePayload(id));
    }

    public static void sendCheatsPermission(ServerPlayerEntity player, boolean cheats)
    {
        ServerPlayNetworking.send(player, new CheatsPermissionPayload(cheats));
    }

    public static void sendSharedForm(ServerPlayerEntity player, MapType data)
    {
        byte[] formData = DataStorageUtils.writeToBytes(data);
        ServerPlayNetworking.send(player, new SharedFormPayload(formData));
    }

    public static void sendEntityForm(ServerPlayerEntity player, IEntityFormProvider actor)
    {
        byte[] formData = DataStorageUtils.writeToBytes(FormUtils.toData(actor.getForm()));
        ServerPlayNetworking.send(player, new EntityFormPayload(actor.getEntityId(), formData));
    }

    public static void sendActors(ServerPlayerEntity player, String filmId, Map<String, LivingEntity> actors)
    {
        int[] entityIds = new int[actors.size()];
        String[] actorKeys = new String[actors.size()];

        int i = 0;
        for (Map.Entry<String, LivingEntity> entry : actors.entrySet())
        {
            actorKeys[i] = entry.getKey();
            entityIds[i] = entry.getValue().getId();
            i++;
        }

        ServerPlayNetworking.send(player, new ActorsPayload(filmId, entityIds, actorKeys));
    }

    public static void sendGunProperties(ServerPlayerEntity player, GunProjectileEntity projectile)
    {
        GunProperties properties = projectile.getProperties();
        byte[] propertiesData = DataStorageUtils.writeToBytes(properties.toData());

        ServerPlayNetworking.send(player, new GunPropertiesPayload(projectile.getId(), propertiesData));
    }

    public static void sendPauseFilm(ServerPlayerEntity player, String filmId)
    {
        ServerPlayNetworking.send(player, new PauseFilmPayload(filmId));
    }

    public static void sendSelectedSlot(ServerPlayerEntity player, int slot)
    {
        player.getInventory().selectedSlot = slot;
        ServerPlayNetworking.send(player, new SelectedSlotPayload(slot));
    }

    public static void sendModelBlockState(ServerPlayerEntity player, BlockPos pos, String trigger)
    {
        ServerPlayNetworking.send(player, new ModelBlockStateTriggerPayload(pos, trigger));
    }

    public static void sendReloadModelBlocks(ServerPlayerEntity player, int tickRandom)
    {
        ServerPlayNetworking.send(player, new RefreshModelBlocksPayload(tickRandom));
    }
}
