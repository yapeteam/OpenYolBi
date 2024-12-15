package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.block.EventNote;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.impl.misc.notebot.MidiReader;
import cn.yapeteam.yolbi.module.impl.misc.notebot.Music;
import cn.yapeteam.yolbi.module.impl.misc.notebot.MusicNote;
import cn.yapeteam.yolbi.module.impl.misc.notebot.NBSReader;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNote;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class NoteBot extends Module {
    private final NumberValue<Integer> scanDelay = new NumberValue<>("Scan Dealy", 100, 0, 2000, 1);

    public NoteBot() {
        super("NoteBot", ModuleCategory.MISC);
        ModeValue<String> list = new ModeValue<>("Music", null);
        BooleanValue refresh = new BooleanValue("Refresh PlayList", false);
        refresh.setCallback((oldV, newV) -> {
            if (newV) {
                File dir = new File(YolBi.YOLBI_DIR, "notebot");
                if (!dir.exists()) {
                    boolean ignored = dir.mkdirs();
                }
                if (dir.list() != null) {
                    list.setModes(Arrays.stream(Objects.requireNonNull(dir.list())).filter(s -> (s.endsWith(".mid") || s.endsWith(".nbs"))).toArray(String[]::new));
                    if (list.getModes().length != 0)
                        list.setValue(list.getModes()[0]);
                }
            }
            return false;
        });
        BooleanValue play = new BooleanValue("Play", false);
        play.setCallback((oldV, newV) -> {
            if (newV && list.getValue() != null) {
                setEnabled(true);
                play(new File(YolBi.YOLBI_DIR, "notebot/" + list.getValue()));
            }
            return false;
        });
        BooleanValue stop = new BooleanValue("Stop", false);
        stop.setCallback((oldV, newV) -> {
            if (newV)
                setEnabled(false);
            return false;
        });
        addValues(list, refresh, scanDelay, play, stop);
    }

    private Music playingMusic;
    private volatile long startTime;
    private final ArrayList<BNIPair> checkedList = new ArrayList<>();
    private final int[] notes = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
    private final int[] inst = new int[]{0, 1, 2, 3, 4};

    private boolean equals(BlockPos a, BlockPos b) {
        if (a == null && b == null) return true;
        else if (a != null && b != null)
            return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
        return false;
    }

    @Listener
    private void onNote(EventNote e) {
        if (playing) return;
        checkedList.stream().filter(p -> p.getPos() == null && !hasPos(checkedList, e.getPos()) && p.getInst() == e.getPitch()).findFirst().ifPresent(pair -> pair.setPos(e.getPos()));
        for (BNIPair pair : checkedList)
            if (equals(pair.getPos(), e.getPos())) {
                pair.setInst(e.getPitch());
                if (pair.getRNote() == -1) {
                    pair.setRNote(e.getNote());
                    if (pair.getNote() != pair.getRNote())
                        pair.setTimes(pair.getNote() > pair.getRNote() ? pair.getNote() - pair.getRNote() : pair.getNote() + 25 - pair.getRNote());
                    else pair.setTimes(0);
                } else pair.setRNote(e.getNote());
            }
    }

    private final ArrayList<BlockPos> clickedList = new ArrayList<>();
    private boolean clicked = false;


    private float[] rotation = new float[]{0, 0};
    private boolean playing = false;

    private BNIPair getBNIPair(ArrayList<BNIPair> bniPairs, int note, int inst) {
        for (BNIPair bniPair : bniPairs)
            if (bniPair.getNote() == note && bniPair.getInst() == inst) return bniPair;
        return null;
    }

    private boolean hasPos(ArrayList<BNIPair> bniPairs, BlockPos pos) {
        for (BNIPair bniPair : bniPairs)
            if (equals(bniPair.getPos(), pos))
                return true;
        return false;
    }

    TimerUtil timerUtil = new TimerUtil();

    private final ArrayList<Packet> clickPackets = new ArrayList<>();

    @Listener

    private void onUpdate(EventMotion e) {
        if (playingMusic == null) {
            stopListening();
            return;
        }
        if (!playing) {
            if (!clicked) {
                int radius = 10;
                BlockPos playerPos = mc.thePlayer.getPosition();
                int startX = playerPos.getX() - radius;
                int startY = playerPos.getY() - radius;
                int startZ = playerPos.getZ() - radius;

                int endX = playerPos.getX() + radius;
                int endY = playerPos.getY() + radius;
                int endZ = playerPos.getZ() + radius;
                for (int x = startX; x <= endX; x++)
                    for (int y = startY; y <= endY; y++)
                        for (int z = startZ; z <= endZ; z++) {
                            BlockPos blockPos = new BlockPos(x, y, z);
                            Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                            if (block instanceof BlockNote) {
                                if (clickedList.contains(blockPos)) continue;
                                clickedList.add(blockPos);
                                // rotation = RotationsUtil.getRotationsToBlockPos(blockPos);
                                // e.setYaw(rotation[0]);
                                // e.setPitch(rotation[1]);
                                //mc.thePlayer.swingItem();
                                //mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));
                                clickPackets.add(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));
                                break;
                            }
                        }
                System.out.println("Raised " + clickPackets.size() + " Packets");
                if (!timerUtil.hasTimePassed(scanDelay.getValue())) return;
                timerUtil.reset();
                if (clickPackets.isEmpty()) {
                    System.out.println("Scanned");
                    clicked = true;
                } else {
                    mc.thePlayer.swingItem();
                    C07PacketPlayerDigging packetPlayerDigging = (C07PacketPlayerDigging) clickPackets.remove(0);
                    mc.getNetHandler().getNetworkManager().sendPacket(packetPlayerDigging);
                    clickedList.add(packetPlayerDigging.getPosition());
                    return;
                }
            }

            boolean complete = true;
            if (timerUtil.hasTimePassed(10)) {
                timerUtil.reset();
                for (BNIPair bniPair : checkedList) {
                    if (bniPair.getTimes() != 0) complete = false;
                    if (bniPair.getTimes() > 0) {
                        if (bniPair.getPos() != null)
                            rotation = RotationsUtil.getRotationsToBlockPos(bniPair.getPos());
                        e.setYaw(rotation[0]);
                        e.setPitch(rotation[1]);
                        mc.thePlayer.swingItem();
                        mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(bniPair.getPos(), 1, mc.thePlayer.inventory.getCurrentItem(), 0.5f, 1, 0.5f));
                        bniPair.setTimes(bniPair.getTimes() - 1);
                        break;
                    }
                }
            } else complete = false;
            if (complete) {
                playing = true;
                startTime = System.currentTimeMillis();
            } else return;
        }

        e.setYaw(rotation[0]);
        e.setPitch(rotation[1]);
        for (int i = 0; i < playingMusic.getNotes().size(); i++) {
            MusicNote note = playingMusic.getNotes().get(i);
            if (note.getTime() <= System.currentTimeMillis() - startTime) {
                BlockPos pos = null;
                BNIPair bniPair = getBNIPair(checkedList, note.getNote(), note.getInst());
                if (bniPair != null) pos = bniPair.getPos();
                else System.err.println("Bad: " + note.getNote() + ", " + note.getInst());
                if (pos != null) {
                    rotation = RotationsUtil.getRotationsToBlockPos(pos);
                    e.setYaw(rotation[0]);
                    e.setPitch(rotation[1]);
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(rotation[0], rotation[1], e.isOnGround()));
                    mc.thePlayer.swingItem();
                    mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
                } else System.err.println("Bad: " + note.getNote() + ", " + note.getInst());
                playingMusic.getNotes().remove(note);
                i--;
            }
        }
        if (playingMusic.getNotes().isEmpty()) {
            playingMusic = null;
            setEnabled(false);
        }
    }

    private void play(File file) {
        try {
            stopListening();
            playingMusic = file.getName().endsWith(".mid") || file.getName().endsWith(".midi") ? MidiReader.read(file) :
                    file.getName().endsWith(".nbs") ? NBSReader.read(file) : null;
            if (playingMusic == null) return;
            playing = false;
            checkedList.clear();
            clickedList.clear();
            clicked = false;
            for (int note : notes) {
                for (int i : inst) {
                    checkedList.add(new BNIPair(null, note, i));
                }
            }

            timerUtil.reset();
            startListening();
        } catch (InvalidMidiDataException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDisable() {
        playingMusic = null;
        clickPackets.clear();
    }

    @Setter
    @Getter
    static class BNIPair {
        public BNIPair(BlockPos pos, int note, int inst) {
            this.pos = pos;
            this.note = note;
            this.inst = inst;
        }

        private BlockPos pos;
        private int note, RNote = -1;
        private int inst;
        private int times;
    }
}
