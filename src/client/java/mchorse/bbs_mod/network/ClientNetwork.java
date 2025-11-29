package mchorse.bbs_mod.network;

import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.actions.ActionState;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.blocks.entities.ModelProperties;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.data.DataStorageUtils;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.entity.GunProjectileEntity;
import mchorse.bbs_mod.entity.IEntityFormProvider;
import mchorse.bbs_mod.film.Film;
import mchorse.bbs_mod.film.Films;
import mchorse.bbs_mod.forms.FormUtils;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.items.GunProperties;
import mchorse.bbs_mod.morphing.Morph;
import mchorse.bbs_mod.network.payloads.BBSPayloads;
import mchorse.bbs_mod.network.payloads.BBSPayloads.*;
import mchorse.bbs_mod.settings.values.base.BaseValue;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.dashboard.UIDashboard;
import mchorse.bbs_mod.ui.film.UIFilmPanel;
import mchorse.bbs_mod.ui.framework.UIBaseMenu;
import mchorse.bbs_mod.ui.framework.UIScreen;
import mchorse.bbs_mod.ui.model_blocks.UIModelBlockPanel;
import mchorse.bbs_mod.ui.morphing.UIMorphingPanel;
import mchorse.bbs_mod.utils.DataPath;
import mchorse.bbs_mod.utils.repos.RepositoryOperation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ClientNetwork
{
    private static int ids = 0;
    private static Map<Integer, Consumer<BaseType>> callbacks = new HashMap<>();

    private static boolean isBBSModOnServer;

    public static void resetHandshake()
    {
        isBBSModOnServer = false;
    }

    public static boolean isIsBBSModOnServer()
    {
        return isBBSModOnServer;
    }

    /* Network */

    public static void setup()
    {
        // Client-bound packet handlers (S2C)
        ClientPlayNetworking.registerGlobalReceiver(ClickedModelBlockPayload.ID, (payload, context) ->
            context.client().execute(() -> handleClientModelBlockPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(PlayerFormPayload.ID, (payload, context) ->
            context.client().execute(() -> handlePlayerFormPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(PlayFilmPayload.ID, (payload, context) ->
            context.client().execute(() -> handlePlayFilmPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(ManagerDataPayload.ID, (payload, context) ->
            context.client().execute(() -> handleManagerDataPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(StopFilmPayload.ID, (payload, context) ->
            context.client().execute(() -> handleStopFilmPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) ->
            context.client().execute(() -> handleHandshakePacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(RecordedActionsPayload.ID, (payload, context) ->
            context.client().execute(() -> handleRecordedActionsPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(AnimationStateTriggerPayload.ID, (payload, context) ->
            context.client().execute(() -> handleFormTriggerPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(CheatsPermissionPayload.ID, (payload, context) ->
            context.client().execute(() -> handleCheatsPermissionPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(SharedFormPayload.ID, (payload, context) ->
            context.client().execute(() -> handleShareFormPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(EntityFormPayload.ID, (payload, context) ->
            context.client().execute(() -> handleEntityFormPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(ActorsPayload.ID, (payload, context) ->
            context.client().execute(() -> handleActorsPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(GunPropertiesPayload.ID, (payload, context) ->
            context.client().execute(() -> handleGunPropertiesPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(PauseFilmPayload.ID, (payload, context) ->
            context.client().execute(() -> handlePauseFilmPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(SelectedSlotPayload.ID, (payload, context) ->
            context.client().execute(() -> handleSelectedSlotPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(ModelBlockStateTriggerPayload.ID, (payload, context) ->
            context.client().execute(() -> handleAnimationStateModelBlockPacket(context.client(), payload)));

        ClientPlayNetworking.registerGlobalReceiver(RefreshModelBlocksPayload.ID, (payload, context) ->
            context.client().execute(() -> handleRefreshModelBlocksPacket(context.client(), payload)));
    }

    /* Handlers */

    private static void handleClientModelBlockPacket(MinecraftClient client, ClickedModelBlockPayload payload)
    {
        BlockPos pos = payload.pos();
        BlockEntity entity = client.world.getBlockEntity(pos);

        if (!(entity instanceof ModelBlockEntity))
        {
            return;
        }

        UIBaseMenu menu = UIScreen.getCurrentMenu();
        UIDashboard dashboard = BBSModClient.getDashboard();

        if (menu != dashboard)
        {
            UIScreen.open(dashboard);
        }

        UIModelBlockPanel panel = dashboard.getPanels().getPanel(UIModelBlockPanel.class);

        dashboard.setPanel(panel);
        panel.fill((ModelBlockEntity) entity, true);
    }

    private static void handlePlayerFormPacket(MinecraftClient client, PlayerFormPayload payload)
    {
        int id = payload.playerId();
        Form form = FormUtils.fromData(DataStorageUtils.readFromBytes(payload.formData()));

        Entity entity = client.world.getEntityById(id);
        Morph morph = Morph.getMorph(entity);

        if (morph != null)
        {
            morph.setForm(form);
        }
    }

    private static void handlePlayFilmPacket(MinecraftClient client, PlayFilmPayload payload)
    {
        String filmId = payload.filmId();
        boolean withCamera = payload.withCamera();
        Film film = new Film();

        film.setId(filmId);
        film.fromData(DataStorageUtils.readFromBytes(payload.filmData()));

        Films.playFilm(film, withCamera);
    }

    private static void handleManagerDataPacket(MinecraftClient client, ManagerDataPayload payload)
    {
        int callbackId = payload.callbackId();
        RepositoryOperation op = RepositoryOperation.values()[payload.operation()];
        BaseType data = DataStorageUtils.readFromBytes(payload.data());

        Consumer<BaseType> callback = callbacks.remove(callbackId);

        if (callback != null)
        {
            callback.accept(data);
        }
    }

    private static void handleStopFilmPacket(MinecraftClient client, StopFilmPayload payload)
    {
        Films.stopFilm(payload.filmId());
    }

    private static void handleHandshakePacket(MinecraftClient client, HandshakePayload payload)
    {
        isBBSModOnServer = true;
    }

    private static void handleRecordedActionsPacket(MinecraftClient client, RecordedActionsPayload payload)
    {
        String filmId = payload.filmId();
        int replayId = payload.replayId();
        int tick = payload.tick();
        BaseType data = DataStorageUtils.readFromBytes(payload.clipsData());

        BBSModClient.getDashboard().getPanels().getPanel(UIFilmPanel.class).receiveActions(filmId, replayId, tick, data);
    }

    private static void handleFormTriggerPacket(MinecraftClient client, AnimationStateTriggerPayload payload)
    {
        int id = payload.entityId();
        String triggerId = payload.trigger();
        int type = payload.type();

        Entity entity = client.world.getEntityById(id);
        Morph morph = Morph.getMorph(entity);

        if (morph != null && morph.getForm() != null)
        {
            morph.getForm().playState(triggerId);
        }

        if (entity instanceof LivingEntity livingEntity && type > 0)
        {
            ItemStack stackInHand = livingEntity.getStackInHand(type == 1 ? Hand.MAIN_HAND : Hand.OFF_HAND);
            ModelProperties properties = BBSModClient.getItemStackProperties(stackInHand);

            if (properties != null && properties.getForm() != null)
            {
                properties.getForm().playState(triggerId);
            }
        }
    }

    private static void handleCheatsPermissionPacket(MinecraftClient client, CheatsPermissionPayload payload)
    {
        client.player.setClientPermissionLevel(payload.allowed() ? 4 : 0);
    }

    private static void handleShareFormPacket(MinecraftClient client, SharedFormPayload payload)
    {
        Form form = FormUtils.fromData(DataStorageUtils.readFromBytes(payload.formData()));

        if (form == null)
        {
            return;
        }

        UIBaseMenu menu = UIScreen.getCurrentMenu();
        UIDashboard dashboard = BBSModClient.getDashboard();

        if (menu == null)
        {
            UIScreen.open(dashboard);
        }

        dashboard.setPanel(dashboard.getPanel(UIMorphingPanel.class));
        BBSModClient.getFormCategories().getRecentForms().getCategories().get(0).addForm(form);
        dashboard.context.notifyInfo(UIKeys.FORMS_SHARED_NOTIFICATION.format(form.getDisplayName()));
    }

    private static void handleEntityFormPacket(MinecraftClient client, EntityFormPayload payload)
    {
        Form form = FormUtils.fromData(DataStorageUtils.readFromBytes(payload.formData()));

        if (form == null)
        {
            return;
        }

        int entityId = payload.entityId();
        Entity entity = client.world.getEntityById(entityId);

        if (entity instanceof IEntityFormProvider provider)
        {
            provider.setForm(form);
        }
    }

    private static void handleActorsPacket(MinecraftClient client, ActorsPayload payload)
    {
        Map<String, Integer> actors = new HashMap<>();
        String filmId = payload.filmId();
        String[] keys = payload.actorKeys();
        int[] entityIds = payload.entityIds();

        for (int i = 0; i < keys.length; i++)
        {
            actors.put(keys[i], entityIds[i]);
        }

        UIDashboard dashboard = BBSModClient.getDashboard();
        UIFilmPanel panel = dashboard.getPanel(UIFilmPanel.class);

        panel.updateActors(filmId, actors);
        BBSModClient.getFilms().updateActors(filmId, actors);
    }

    private static void handleGunPropertiesPacket(MinecraftClient client, GunPropertiesPayload payload)
    {
        GunProperties properties = new GunProperties();
        int entityId = payload.entityId();

        properties.fromData((MapType) DataStorageUtils.readFromBytes(payload.propertiesData()));

        Entity entity = client.world.getEntityById(entityId);

        if (entity instanceof GunProjectileEntity projectile)
        {
            projectile.setProperties(properties);
            projectile.calculateDimensions();
        }
    }

    private static void handlePauseFilmPacket(MinecraftClient client, PauseFilmPayload payload)
    {
        Films.togglePauseFilm(payload.filmId());
    }

    private static void handleSelectedSlotPacket(MinecraftClient client, SelectedSlotPayload payload)
    {
        client.player.getInventory().selectedSlot = payload.slot();
    }

    private static void handleAnimationStateModelBlockPacket(MinecraftClient client, ModelBlockStateTriggerPayload payload)
    {
        BlockPos pos = payload.pos();
        String state = payload.trigger();

        BlockEntity blockEntity = client.world.getBlockEntity(pos);

        if (blockEntity instanceof ModelBlockEntity block)
        {
            if (block.getProperties().getForm() != null)
            {
                block.getProperties().getForm().playState(state);
            }
        }
    }

    private static void handleRefreshModelBlocksPacket(MinecraftClient client, RefreshModelBlocksPayload payload)
    {
        int range = payload.tickRandom();

        for (ModelBlockEntity mb : BBSRendering.capturedModelBlocks)
        {
            ModelProperties properties = mb.getProperties();
            int random = (int) (Math.random() * range);

            properties.setForm(FormUtils.copy(properties.getForm()));

            while (random > 0)
            {
                properties.update(mb.getEntity());
                random -= 1;
            }
        }
    }

    /* API */

    public static void sendModelBlockForm(BlockPos pos, ModelBlockEntity modelBlock)
    {
        byte[] formData = DataStorageUtils.writeToBytes(modelBlock.getProperties().toData());
        ClientPlayNetworking.send(new ModelBlockFormRequestPayload(pos, formData));
    }

    public static void sendPlayerForm(Form form)
    {
        MapType mapType = FormUtils.toData(form);
        byte[] formData = DataStorageUtils.writeToBytes(mapType == null ? new MapType() : mapType);
        ClientPlayNetworking.send(new PlayerFormRequestPayload(formData));
    }

    public static void sendModelBlockTransforms(MapType data)
    {
        byte[] transformsData = DataStorageUtils.writeToBytes(data);
        ClientPlayNetworking.send(new ModelBlockTransformsRequestPayload(transformsData));
    }

    public static void sendManagerDataLoad(String id, Consumer<BaseType> consumer)
    {
        MapType mapType = new MapType();

        mapType.putString("id", id);
        ClientNetwork.sendManagerData(RepositoryOperation.LOAD, mapType, consumer);
    }

    public static void sendManagerData(RepositoryOperation op, BaseType data, Consumer<BaseType> consumer)
    {
        int id = ids;

        callbacks.put(id, consumer);
        sendManagerData(id, op, data);

        ids += 1;
    }

    public static void sendManagerData(int callbackId, RepositoryOperation op, BaseType data)
    {
        byte[] dataBytes = DataStorageUtils.writeToBytes(data);
        ClientPlayNetworking.send(new ManagerDataRequestPayload(callbackId, op.ordinal(), dataBytes));
    }

    public static void sendActionRecording(String filmId, int replayId, int tick, int countdown, boolean state)
    {
        ClientPlayNetworking.send(new ActionRecordingPayload(filmId, replayId, tick, countdown, state));
    }

    public static void sendToggleFilm(String filmId, boolean withCamera)
    {
        ClientPlayNetworking.send(new ToggleFilmPayload(filmId, withCamera));
    }

    public static void sendActionState(String filmId, ActionState state, int tick)
    {
        ClientPlayNetworking.send(new ActionControlPayload(filmId, (byte) state.ordinal(), tick));
    }

    public static void sendSyncData(String filmId, BaseValue data)
    {
        DataPath path = data.getPath();
        String[] pathArray = path.strings.toArray(new String[0]);
        byte[] dataBytes = DataStorageUtils.writeToBytes(data.toData());

        ClientPlayNetworking.send(new FilmDataSyncPayload(filmId, pathArray, dataBytes));
    }

    public static void sendTeleport(PlayerEntity entity, double x, double y, double z)
    {
        sendTeleport(x, y, z, entity.getHeadYaw(), entity.getHeadYaw(), entity.getPitch());
    }

    public static void sendTeleport(double x, double y, double z, float yaw, float bodyYaw, float pitch)
    {
        ClientPlayNetworking.send(new PlayerTeleportPayload(x, y, z, yaw, bodyYaw, pitch));
    }

    public static void sendFormTrigger(String triggerId, int type)
    {
        ClientPlayNetworking.send(new AnimationStateTriggerRequestPayload(triggerId, type));
    }

    public static void sendSharedForm(Form form, UUID uuid)
    {
        MapType mapType = FormUtils.toData(form);
        byte[] formData = DataStorageUtils.writeToBytes(mapType == null ? new MapType() : mapType);
        ClientPlayNetworking.send(new ShareFormPayload(uuid, formData));
    }

    public static void sendZoom(boolean zoom)
    {
        ClientPlayNetworking.send(new ZoomPayload(zoom));
    }

    public static void sendPauseFilm(String filmId)
    {
        ClientPlayNetworking.send(new PauseFilmRequestPayload(filmId));
    }
}
