/*
 * Copyright (c) September 2017 FTC Teams 25/5218
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted (subject to the limitations in the disclaimer below) provided that
 *  the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this list
 *  of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice, this
 *  list of conditions and the following disclaimer in the documentation and/or
 *  other materials provided with the distribution.
 *
 *  Neither the name of FTC Teams 25/5218 nor the names of their contributors may be used to
 *  endorse or promote products derived from this software without specific prior
 *  written permission.
 *
 *  NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 *  LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 *  TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package team25core;

import java.util.LinkedList;
import java.util.Queue;

public class DeadReckonPath {

    /*
     * FIXME: Add a pivot turn segment type
     */
    public enum SegmentType {
        STRAIGHT,
        TURN,
        SIDEWAYS,
        LEFT_DIAGONAL,
        RIGHT_DIAGONAL,
    }

    public enum SegmentState {
        INITIALIZE,
        ENCODER_RESET,
        SET_TARGET,
        CONSUME_SEGMENT,
        ENCODER_TARGET,
        STOP_MOTORS,
        WAIT,
        DONE,
    }

    protected enum TurnKind {
        TURN_USING_ENCODERS,
        TURN_USING_GYRO,
    }

    public Queue<Segment> segments;
    protected Segment currSegment;
    protected boolean setup;
    protected int target;
    protected PersistentTelemetryTask ptt;

    public class Segment {

        public SegmentType type;
        public SegmentState state;
        public double distance;
        public double speed;

        Segment(SegmentType type, double distance, double speed)
        {
            this.state = SegmentState.INITIALIZE;
            this.distance = distance;
            this.type = type;
            this.speed = speed;
        }
    }

    public DeadReckonPath()
    {
        this.currSegment = null;
        segments = new LinkedList<Segment>();
    }

    public int numSegments()
    {
        return segments.size();
    }

    public void addSegment(SegmentType type, double distance, double speed)
    {
        segments.add(new Segment(type, distance, speed));
    }

    public void nextSegment()
    {
        segments.remove();
    }

    public Segment getCurrentSegment()
    {
        return segments.peek();
    }

    public void stop()
    {
        /*
         * Remove all remaining segments.
         */
        segments.clear();
    }
}

